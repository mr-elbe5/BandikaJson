package de.elbe5.tag;

import de.elbe5.base.data.StringUtil;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;

public class MessageHtml {

    final static String messageHtml = """
        <div class="alert alert-{1} alert-dismissible fade show" role="alert">
            {2}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        """;

    public static String getHtml(RequestData rdata) {
        if (!rdata.hasMessage())
            return "";
        String msg = rdata.getString(RequestData.KEY_MESSAGE);
        String msgType = rdata.getString(RequestData.KEY_MESSAGETYPE);
        return(StringUtil.format(messageHtml, msgType, StringUtil.toHtml(msg)));
    }

}
