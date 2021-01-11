package de.elbe5.contentcontrol;

import de.elbe5.application.Application;
import de.elbe5.base.data.StringUtil;
import de.elbe5.content.ContentData;
import de.elbe5.rights.ContentRights;
import de.elbe5.user.UserData;

public class Footer {

    final static String footerStart = """
            <ul class="nav">
                <li class="nav-item">
                    <a class="nav-link">&copy; {1}
                    </a>
                </li>
                """;

    final static String footerLink = """           
                <li class="nav-item">
                    <a class="nav-link" href="{1}">{2}
                    </a>
                </li>
                """;

    final static String footerEnd = """
            </ul>
                """;

    public static String getHtml(ContentData currentContent, UserData currentUser) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.format(footerStart,
                StringUtil.toHtml(Application.getConfiguration().getCopyright())
        ));
        for (ContentData data : Application.getContent().getContentRoot().getChildren()) {
            if (data.getNavType().equals(ContentData.NAV_TYPE_FOOTER) && ContentRights.hasUserReadRight(currentUser, currentContent)) {
                sb.append(StringUtil.format(footerLink,
                        data.getUrl(),
                        StringUtil.toHtml(data.getDisplayName())
                ));
            }
        }
        sb.append(footerEnd);
        return sb.toString();
    }

}
