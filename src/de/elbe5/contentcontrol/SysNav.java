package de.elbe5.contentcontrol;

import de.elbe5.application.Strings;
import de.elbe5.base.data.StringUtil;
import de.elbe5.content.ContentData;
import de.elbe5.content.EditableContentData;
import de.elbe5.content.ViewType;
import de.elbe5.rights.ContentRights;
import de.elbe5.rights.SystemRights;
import de.elbe5.user.UserData;

import java.util.Locale;

public class SysNav {

    final static String sysNavStart = """
            <ul class="nav justify-content-end">
            """;
    final static String adminLink = """
            <li class="nav-item">
                <a class="nav-link fa fa-cog" href="/ctrl/admin/openContentAdministration" title="{1}"></a>
            </li>
            """;
    final static String editLink = """
            <li class="nav-item">
                <a class="nav-link fa fa-edit" href="/ctrl/{1}/openEditPage/{2}" title="{3}"></a>
            </li>
            """;
    final static String draftLink = """
                <li class="nav-item">
                    <a class="nav-link fa fa-eye-slash" href="/ctrl/{1}/showDraft/{2}" title="{3}" ></a>
                </li>
            """;
    final static String publishedLink = """
            <li class="nav-item">
                <a class="nav-link fa fa-eye" href="/ctrl/{1}/showPublished/{2}" title="{3}"></a>
            </li>
            """;
    final static String approveLink = """
            <li class="nav-item">
                <a class="nav-link fa fa-thumbs-up" href="/ctrl/{1}/publishPage/{2}" title="{3}"></a>
            </li>
            """;
    final static String searchLink = """
            <li>
                <a class="nav-link fa fa-search" href="/ctrl/search/openSearch" title="{1}">
                </a>
            </li>
            """;
    final static String dropDownStart = """
            <li class="nav-item">
                <a class="nav-link fa {1}" data-toggle="dropdown" title="{2}"></a>
                <div class="dropdown-menu">
                """;
    final static String profileLink = """
            <a class="dropdown-item" href="/ctrl/user/openProfile">{1}
            </a>
            """;
    final static String logoutLink = """
            <a class="dropdown-item" href="/ctrl/user/logout">{1}
            </a>
            """;
    final static String loginLink = """
            <a class="dropdown-item" href="" onclick="return openModalDialog('/ajax/user/openLogin');">{1}
            </a>
            """;
    final static String sysNavEnd = """
                    </div>
                </li>
            </ul>
            """;

    public static String getHtml(ContentData currentContent, ViewType viewType, UserData currentUser, boolean isLoggedIn, Locale locale, boolean useSearch) {
        StringBuilder sb = new StringBuilder();
        sb.append(sysNavStart);
        if (isLoggedIn) {
            if (SystemRights.hasUserAnySystemRight(currentUser)) {
                sb.append(StringUtil.format(adminLink,
                        Strings.html("_administration", locale)

                ));
            }
            if (currentContent instanceof EditableContentData) {
                EditableContentData editableContent = (EditableContentData) currentContent;
                if (!viewType.equals(ViewType.edit) && ContentRights.hasUserEditRight(currentUser, currentContent)) {
                    sb.append(StringUtil.format(editLink,
                            currentContent.getTypeKey(),
                            Integer.toString(currentContent.getId()),
                            Strings.html("_editPage", locale)
                    ));
                    if (editableContent.hasUnpublishedDraft()) {
                        if (editableContent.isPublished()) {
                            if (viewType.equals(ViewType.showPublished)) {
                                sb.append(StringUtil.format(draftLink,
                                        currentContent.getTypeKey(),
                                        Integer.toString(currentContent.getId()),
                                        Strings.html("_showDraft", locale)
                                ));
                            } else {
                                sb.append(StringUtil.format(publishedLink,
                                        currentContent.getTypeKey(),
                                        Integer.toString(currentContent.getId()),
                                        Strings.html("_showPublished", locale)
                                ));
                            }
                        }
                        if (ContentRights.hasUserApproveRight(currentUser, currentContent)) {
                            sb.append(StringUtil.format(approveLink,
                                    currentContent.getTypeKey(),
                                    Integer.toString(currentContent.getId()),
                                    Strings.html("_publish", locale)
                            ));
                        }
                    }
                }
            }
        }
        if (useSearch) {
            sb.append(StringUtil.format(searchLink,
                    Strings.html("_search", locale)
            ));
        }
        String userClass = isLoggedIn ? "fa-user" : "fa-user-o";
        sb.append(StringUtil.format(dropDownStart,
                userClass,
                Strings.html("_user", locale)
        ));
        if (isLoggedIn) {
            sb.append(StringUtil.format(profileLink,
                    Strings.html("_profile", locale)
            ));
            sb.append(StringUtil.format(logoutLink,
                    Strings.html("_logout", locale)
            ));

        } else {
            sb.append(StringUtil.format(loginLink,
                    Strings.html("_login", locale)
            ));
        }
        sb.append(sysNavEnd);
        return sb.toString();
    }

}
