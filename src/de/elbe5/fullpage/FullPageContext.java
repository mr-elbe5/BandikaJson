/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.fullpage;

import de.elbe5.content.ContentViewContext;
import de.elbe5.content.ViewType;

public class FullPageContext extends ContentViewContext {

    public FullPageContext(FullPageData content, ViewType viewType){
        super(content, viewType);
    }

    public FullPageData getPage() {
        return getContentData(FullPageData.class);
    }

}
