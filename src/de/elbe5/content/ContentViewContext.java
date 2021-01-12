package de.elbe5.content;

public class ContentViewContext {

    private ContentData contentData;
    private ViewType viewType = ViewType.show;

    public ContentViewContext(ContentData contentData, ViewType viewType){
        this.contentData = contentData;
        this.viewType = viewType;

    }

    public ContentData getContentData() {
        return contentData;
    }

    public <T extends ContentData> T getContentData(Class<T> cls) {
        try {
            return cls.cast(contentData);
        }
        catch (NullPointerException | ClassCastException e){
            return null;
        }
    }

    public ViewType getViewType() {
        return viewType;
    }

}
