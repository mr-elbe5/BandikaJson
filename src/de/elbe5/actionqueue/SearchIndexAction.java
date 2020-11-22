/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.actionqueue;

import de.elbe5.application.Application;
import de.elbe5.base.data.StringUtil;
import de.elbe5.base.log.Log;
import de.elbe5.search.SearchController;

public class SearchIndexAction extends RegularAction {

    private static final SearchIndexAction instance = new SearchIndexAction();

    public static SearchIndexAction getInstance() {
        return instance;
    }

    public static void register(){
        RegularAction.getRegularActions().add(getInstance());
    }

    public static void addToQueue(){
        ActionQueue.addAction(getInstance());
    }

    public SearchIndexAction(){
        Log.log("creating searchtask");
    }

    public void execute() {
        Log.log("Indexing");
        SearchController.getInstance().indexPages();
    }

    @Override
    public int getIntervalMinutes() {
        return Application.getConfiguration().getIndexInterval();
    }

}
