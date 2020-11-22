/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.actionqueue.CheckDataAction;
import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class DataContainer implements JsonData {

    protected enum keys{
        changeDate,
        version
    }

    protected LocalDateTime changeDate = null;
    protected int version = 1;

    protected boolean changed = true;

    protected final Lock dataLock = new ReentrantLock();

    // constructors and initializers

    public DataContainer(){
    }

    public abstract boolean initialize();

    // json methods

    public abstract JSONObject toJSONObject();

    public abstract void fromJSONObject(JSONObject obj);

    protected void addJSONAttributes(JSONObject obj){
        obj.put(DataContainer.keys.changeDate.name(), jsonString(changeDate));
        obj.put(DataContainer.keys.version.name(), version);
    }

    protected void getJSONAttributes(JSONObject obj){
        changeDate = getLocalDateTime(obj.optString(DataContainer.keys.changeDate.name()));
        version = obj.optInt(DataContainer.keys.version.name());
    }

    // getter and setter

    //general

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public void increaseVersion() {
        version++;
    }

    public int getVersion() {
        return version;
    }

    //persistance

    public boolean checkChanged() {
        boolean result = false;
        try {
            dataLock.lock();
            if (changed) {
                Log.log(("data has changed"));
                if (save()) {
                    Log.log(("data saved"));
                    changed = false;
                    result = true;
                }
            }
        }
        finally{
            dataLock.unlock();
        }
        return result;
    }

    public synchronized void setHasChanged() {
        if (!changed) {
            increaseVersion();
            setChangeDate(Application.getCurrentTime());
            changed = true;
            CheckDataAction.addToQueue();
        }
    }

    protected abstract boolean save();

}
