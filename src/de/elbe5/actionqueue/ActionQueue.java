/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.actionqueue;

import de.elbe5.application.Application;
import de.elbe5.application.ApplicationContextListener;
import de.elbe5.base.log.Log;
import de.elbe5.base.thread.BaseThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActionQueue {

    private static final List<QueuedAction> actionList = Collections.synchronizedList(new LinkedList<>());
    private static final Object lockObj = 1;
    private static ActionThread actionThread = null;

    public static void start() {
        if (actionThread == null) {
            synchronized (lockObj) {
                if (actionThread == null) {
                    actionThread = new ActionThread("ActionThread");
                    actionThread.startRunning();
                    ApplicationContextListener.registerThread(actionThread);
                }
            }
        }
    }

    public void stop() {
        if (actionThread != null) {
            actionThread.stopRunning();
            actionThread.interrupt();
        }
    }

    public static void addAction(QueuedAction action) {
        //Log.log("adding to queue: " + action.getClass().getSimpleName());
        if (!actionList.contains(action)) {
            actionList.add(action);
        }
    }

    public List<QueuedAction> getActions() {
        return new ArrayList<>(actionList);
    }

    private static class ActionThread extends BaseThread {

        public ActionThread(String name) {
            super(name);
        }

        public void run() {
            while (running) {
                RegularAction.checkRegularActions();
                QueuedAction action = null;
                try {
                    if (!actionList.isEmpty()){
                        action = actionList.remove(0);
                        //Log.log("executing: " + action.getClass().getSimpleName());
                        action.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (action == null) {
                    if (!running)
                        break;
                    try {
                        Thread.sleep(Application.getConfiguration().getTimerInterval());
                    } catch (InterruptedException e) {
                        running = false;
                        if (actionList.isEmpty())
                            break;
                    }
                }
            }
            actionThread = null;
        }

    }

}
