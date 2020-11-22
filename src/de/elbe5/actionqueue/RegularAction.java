/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.actionqueue;

import de.elbe5.application.Application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class RegularAction extends QueuedAction{

    private static final List<RegularAction> regularActions = new ArrayList<>();

    public static List<RegularAction> getRegularActions() {
        return regularActions;
    }

    public static void checkRegularActions(){
        for (RegularAction action : regularActions){
            action.checkNextExecution();
        }
    }

    protected LocalDateTime nextExecution;

    // constructors

    public RegularAction(){
        nextExecution = Application.getCurrentTime();
    }

    // defaults

    public abstract int getIntervalMinutes();

    public LocalDateTime getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(LocalDateTime nextExecution) {
        this.nextExecution = nextExecution;
    }

    public boolean isActive() {
        return getIntervalMinutes() != 0;
    }

    // other methods

    public void checkNextExecution() {
        if (isActive()) {
            LocalDateTime now = Application.getCurrentTime();
            LocalDateTime next = getNextExecution();
            if (now.isAfter(next)) {
                ActionQueue.addAction(this);
                setNextExecution(now.plusMinutes(getIntervalMinutes()));
            }
        }
    }

}
