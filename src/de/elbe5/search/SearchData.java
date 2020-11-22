/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;

import java.io.StringReader;

public class SearchData {

    public static final String TYPE_KEY = "search";

    protected static final int CONTEXT_LENGTH_NAME = 100;
    protected static final int CONTEXT_LENGTH_DESCRIPTION = 200;
    protected static final int CONTEXT_LENGTH_KEYWORDS = 80;
    protected static final int CONTEXT_LENGTH_AUTHOR = 40;
    protected static final int CONTEXT_LENGTH_CONTENT = 80;

    protected Document doc = null;
    protected int id = 0;
    protected String name = "";
    protected String nameContext = "";
    protected String url = "";
    protected String description = "";
    protected String descriptionContext = "";
    protected String keywords = "";
    protected String keywordsContext = "";
    protected String authorName = "";
    protected String authorContext = "";
    protected String content = "";
    protected String contentContext = "";
    protected boolean anonymous = false;

    public void setDoc() {
        doc = new Document();
        doc.add(new Field("id", Integer.toString(getId()), TextField.TYPE_STORED));
        doc.add(new Field("url", getUrl(), TextField.TYPE_STORED));
        doc.add(new Field("name", getName(), TextField.TYPE_STORED));
        doc.add(new Field("description", getDescription(), TextField.TYPE_STORED));
        doc.add(new Field("keywords", getKeywords(), TextField.TYPE_STORED));
        doc.add(new Field("authorName", getAuthorName(), TextField.TYPE_STORED));
        doc.add(new Field("content", getContent(), TextField.TYPE_STORED));
        doc.add(new Field("anonymous", hasOpenAccess() ? "true" : "false", TextField.TYPE_STORED));
    }

    public String getTypeKey(){
        return SearchData.TYPE_KEY;
    }

    public String getKey() {
        return Integer.toString(id);
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public void evaluateDoc() {
        if (doc == null)
            return;
        id = Integer.parseInt(doc.get("id"));
        url = doc.get("url");
        name = doc.get("name");
        description = doc.get("description");
        keywords = doc.get("keywords");
        authorName = doc.get("authorName");
        content = doc.get("content");
        anonymous = doc.get("anonymous").equalsIgnoreCase("true");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameContext() {
        return nameContext;
    }

    public void setNameContext(String nameContext) {
        this.nameContext = nameContext;
    }

    public String getContext(Highlighter highlighter, Analyzer analyzer, String fieldName, int contextLength) {
        highlighter.setTextFragmenter(new SimpleFragmenter(contextLength));
        TokenStream tokenStream = null;
        String text = getDoc().get(fieldName);
        if (text != null && text.length() > 0)
            tokenStream = analyzer.tokenStream(fieldName, new StringReader(text));
        try {
            text = tokenStream == null ? "" : highlighter.getBestFragments(tokenStream, text, 1, "...");
        } catch (Exception ignore) {
        }
        return text;
    }

    public void setContexts(Query query, Analyzer analyzer) {
        Highlighter highlighter = new Highlighter(new SearchContextFormatter(), new QueryScorer(query));
        String context = getContext(highlighter, analyzer, "name", CONTEXT_LENGTH_NAME);
        setNameContext(context == null || context.length() == 0 ? getName() : context);
        context = getContext(highlighter, analyzer, "description", CONTEXT_LENGTH_DESCRIPTION);
        setDescriptionContext(context);
        context = getContext(highlighter, analyzer, "keywords", CONTEXT_LENGTH_KEYWORDS);
        setKeywordsContext(context);
        context = getContext(highlighter, analyzer, "authorName", CONTEXT_LENGTH_AUTHOR);
        setAuthorContext(context);
        context = getContext(highlighter, analyzer, "content", CONTEXT_LENGTH_CONTENT);
        setContentContext(context);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionContext() {
        return descriptionContext;
    }

    public void setDescriptionContext(String descriptionContext) {
        this.descriptionContext = descriptionContext;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setKeywordsContext(String keywordsContext) {
        this.keywordsContext = keywordsContext;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorContext(String authorContext) {
        this.authorContext = authorContext;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentContext() {
        return contentContext;
    }

    public void setContentContext(String contentContext) {
        this.contentContext = contentContext;
    }

    public boolean hasOpenAccess() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

}

