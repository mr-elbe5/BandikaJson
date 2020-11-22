/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import de.elbe5.application.Application;
import de.elbe5.application.ApplicationPath;
import de.elbe5.application.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentData;
import de.elbe5.content.JspContentData;
import de.elbe5.request.RequestData;
import de.elbe5.response.ContentResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;
import de.elbe5.rights.ContentRights;
import de.elbe5.rights.SystemRights;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.ControllerCache;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import java.nio.file.Paths;

public class SearchController extends Controller {

    private static SearchController instance = null;

    public static void setInstance(SearchController instance) {
        SearchController.instance = instance;
    }

    public static SearchController getInstance() {
        return instance;
    }

    public static void register(SearchController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return SearchData.TYPE_KEY;
    }

    public IResponse openSearch(SessionRequestData rdata) {
        SearchResultData contentResult = new SearchResultData();
        rdata.put("searchResultData", contentResult);
        return showSearch();
    }

    public IResponse search(SessionRequestData rdata) {
        SearchResultData contentResult = new SearchResultData();
        String pattern = rdata.getString("searchPattern");
        contentResult.setPattern(pattern);
        searchContent(contentResult);
        for (int i = contentResult.results.size() - 1; i >= 0; i--) {
            SearchData result = contentResult.results.get(i);
            if (!result.hasOpenAccess() && !ContentRights.hasUserReadRight(rdata.getCurrentUser(),result.getId()))
                contentResult.results.remove(i);
        }
        rdata.put("searchResultData", contentResult);
        return showSearch();
    }

    public IResponse indexAllContent(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM));
        SearchController.getInstance().indexPages();
        rdata.setMessage(Strings.string("_indexingContentQueued",rdata.getLocale()), RequestData.MESSAGE_TYPE_SUCCESS);
        return new ForwardResponse("/ctrl/admin/openSystemAdministration");
    }

    protected IResponse showSearch() {
        JspContentData contentData = new JspContentData("/WEB-INF/_jsp/search/search.jsp");
        return new ContentResponse(contentData);
    }

    public void indexPages() {
        try (IndexWriter writer = openIndexWriter()) {
            indexContent(writer);
        } catch (Exception e) {
            Log.error("error while writing content index", e);
        }
    }

    protected IndexWriter openIndexWriter() throws Exception {
        String indexPath = ApplicationPath.getAppSearchIndexPath();
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        return new IndexWriter(dir, iwc);
    }

    protected void indexContent(IndexWriter writer) throws Exception {
        int count = 0;
        for (ContentData content : Application.getContent().getContents()) {
            SearchData data = new SearchData();
            data.setId(content.getId());
            data.setUrl(content.getUrl());
            data.setName(content.getDisplayName());
            data.setDescription(content.getDescription());
            data.setAuthorName(Application.getUsers().getUser(content.getChangerId()).getName());
            data.setContent(content.getSearchContent());
            data.setAnonymous(content.getAccessType().equals(ContentData.ACCESS_TYPE_OPEN));
            data.setDoc();
            writer.addDocument(data.getDoc());
            count++;
            if ((count % 100) == 0) {
                writer.commit();
            }
        }
        writer.commit();
        //Log.log("finished indexing " + count + " contents");
    }

    public void searchContent(SearchResultData result) {
        result.getResults().clear();
        String[] fieldNames = result.getFieldNames();
        ScoreDoc[] hits = null;
        try {
            String indexPath = ApplicationPath.getAppSearchIndexPath();
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(fieldNames, analyzer);
            String pattern = result.getPattern();
            pattern = pattern.trim();
            Query query = null;
            if (pattern.length() != 0) {
                query = parser.parse(pattern);
                //Log.log("Searching for: " + query.toString());
                TopDocs topDocs = searcher.search(query, result.getMaxSearchResults());
                hits = topDocs.scoreDocs;
            }
            if (hits != null) {
                for (ScoreDoc hit : hits) {
                    Document doc = searcher.doc(hit.doc);
                    SearchData data = new SearchData();
                    data.setDoc(doc);
                    data.evaluateDoc();
                    data.setContexts(query, analyzer);
                    result.getResults().add(data);
                }
            }
            reader.close();
        } catch (Exception ignore) {
        }
    }

}
