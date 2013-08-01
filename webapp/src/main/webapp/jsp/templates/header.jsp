<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- Site's Menu bar -->
<div id="siteMenuBar">
  <div id="siteMenuBarFirstColumn">
    <div class="siteMenuBarWidget" id="siteMenuBarWidgetIndex"><a href="${pageContext.request.contextPath}/"><fmt:message key="generic.header.menuBarWidgetIndexLabel"/></a></div>
    <div class="siteMenuBarWidget" id="siteMenuBarWidgetForge"><a href="${pageContext.request.contextPath}/forge/"><fmt:message key="generic.header.menuBarWidgetForgeLabel"/></a></div>
    <div class="siteMenuBarWidget siteMenuBarWidgetDisabled" id="siteMenuBarWidgetIllusion"><a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetIllusionLabel"/></a></div>
    <div class="siteMenuBarWidget siteMenuBarWidgetDisabled" id="siteMenuBarWidgetStore"><a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetStoreLabel"/></a></div>
    <div class="siteMenuBarWidget" id="siteMenuBarWidgetForum"><a href="${pageContext.request.contextPath}/forum/"><fmt:message key="generic.header.menuBarWidgetForumLabel"/></a></div>
  </div>
  <div id="siteMenuBarSecondColumn">
    <c:choose>
      <c:when test="${(loggedUser ne null) and (loggedUser.role ne 'GUEST')}">
        <div class="siteMenuBarWidget" id="siteMenuBarWidgetMessages">
          <a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetMessagesLabel"/></a>
          <c:if test="${newMessages gt 0}">
            <div class="siteMenuBarWidgetNotification">${newMessages}</div>
          </c:if>
        </div>
      </c:when>
      <c:otherwise>
        <div class="siteMenuBarWidget siteMenuBarWidgetDisabled" id="siteMenuBarWidgetMessages">
          <a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetMessagesLabel"/></a>
          <c:if test="${newMessages gt 0}">
            <div class="siteMenuBarWidgetNotification">${newMessages}</div>
          </c:if>
        </div>
      </c:otherwise>
    </c:choose>
  
    <div class="siteMenuBarWidget" id="siteMenuBarWidgetChat"><a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetChatLabel"/></a></div>
    <div class="siteMenuBarWidget" id="siteMenuBarWidgetFriends"><a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetFriendsLabel"/></a></div>
    <div class="siteMenuBarWidget siteMenuBarWidgetDisabled" id="siteMenuBarWidgetCalendar"><a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetCalendarLabel"/></a></div>
  
    <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetMessagesContent">

      <div id="siteMenuBarWidgetMessagesFoldersContainer">
        <div id="siteMenuBarWidgetMessagesButtonsContainer">
          <input type="submit" id="siteMenuBarWidgetMessagesComposeButton" value="<fmt:message key="generic.header.menuBarWidgetMessagesComposeMessageLabel"/>"/>
        </div>
      
        <div id="siteMenuBarWidgetMessagesSpecialFoldersContainer">
          <div class="siteMenuBarWidgetMessagesFolder siteMenuBarWidgetMessagesSelectedFolder" id="siteMenuBarWidgetMessagesInboxFolder">
            <input type="hidden" name="folderId" value="INBOX"/>
            <div class="siteMenuBarWidgetMessagesFolderIcon"></div>
            <div class="siteMenuBarWidgetMessagesFolderName"><fmt:message key="generic.header.menuBarWidgetMessagesInboxFolder"/></div>
            <div class="siteMenuBarWidgetMessagesFolderUnread">${newMessages}</div>
          </div>
          
          <div class="siteMenuBarWidgetMessagesFolder" id="siteMenuBarWidgetMessagesStarredFolder">
            <input type="hidden" name="folderId" value="STARRED"/>
            <div class="siteMenuBarWidgetMessagesFolderIcon"></div>
            <div class="siteMenuBarWidgetMessagesFolderName"><fmt:message key="generic.header.menuBarWidgetMessagesStarredFolder"/></div>
          </div>
          
          <div class="siteMenuBarWidgetMessagesFolder" id="siteMenuBarWidgetMessagesOutboxFolder">
            <input type="hidden" name="folderId" value="OUTBOX"/>
            <div class="siteMenuBarWidgetMessagesFolderIcon"></div>
            <div class="siteMenuBarWidgetMessagesFolderName"><fmt:message key="generic.header.menuBarWidgetMessagesSentFolder"/></div>
          </div>
          
          <div class="siteMenuBarWidgetMessagesFolder" id="siteMenuBarWidgetMessagesTrashFolder">
            <input type="hidden" name="folderId" value="TRASH"/>
            <div class="siteMenuBarWidgetMessagesFolderIcon"></div>
            <div class="siteMenuBarWidgetMessagesFolderName"><fmt:message key="generic.header.menuBarWidgetMessagesTrashFolder"/></div>
          </div>
        </div>

        <div id="siteMenuBarWidgetMessagesCustomFoldersContainer"></div>
      </div>
      
      <div id="siteMenuBarWidgetMessagesWorkareaContainer">
      </div>
   
    </div>
  
    <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetChatContent">
      <div id="siteMenuBarWidgetChatTabsContainer">
        <div class="siteMenuBarWidgetChatInstructions">
          <div class="siteMenuBarWidgetChatWelcomeTitle">
            <fmt:message key="generic.header.menuBarWidgetChatWelcomeTitle"/>
          </div>
          <div class="siteMenuBarWidgetChatWelcomeText">
            <fmt:message key="generic.header.menuBarWidgetChatWelcomeText"/>
          </div>
        </div>
        <ul>
        </ul>
      </div>
      
      <div id="siteMenuBarWidgetChatOnlineFriendsContainer">
        <div class="siteMenuBarWidgetChatNoFriendsOnline">
          <fmt:message key="generic.header.menuBarWidgetChatNoFriendsOnline"/>
        </div>
      </div>
    </div>
    

    <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetFriendsContent">
      <div class="siteMenuBarWidgetFriendsTabsContainer">
        <ul> 
          <li><a href="#sitemenubarfriendswidget-suggest"><fmt:message key="generic.header.menuBarWidgetFriendsSuggestTabLabel"/></a></li>
          <li><a href="#sitemenubarfriendswidget-add"><fmt:message key="generic.header.menuBarWidgetFriendsAddTabLabel"/></a></li>
          <li><a href="#sitemenubarfriendswidget-list"><fmt:message key="generic.header.menuBarWidgetFriendsListTabLabel"/></a></li>
        </ul>
        
        <div id="sitemenubarfriendswidget-suggest">
          <div class="sitemenubarfriendswidgetSuggestContent">
          
          </div>
          <a class="sitemenubarfriendswidgetSuggestMore" href="javascript:void(null);">
            <fmt:message key="generic.header.menuBarWidgetFriendsSuggestMore"/>
          </a>
        </div>
        <div id="sitemenubarfriendswidget-add">
          <!-- 
          <div class="siteMenuBarFriendsWidgetInviteFriendsProvider" id="siteMenuBarFriendsWidgetInviteFriendsProviderGoogle">
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderInfo">
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderImage"></div>
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderText"><fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromGoogle"/></div>
            </div>
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderOptions" style="display: none">
               <div class="siteMenuBarFriendsWidgetInviteFriendsProviderNoStoreText"><fmt:message key="generic.header.menuBarWidgetFriendsAddWillNotStoreCredentials"/></div>
               <div><fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromGoogleUsername"/></div>
               <input type="text" name="googleUsername"/>
               <div><fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromGooglePassword"/></div>
               <input type="text" name="googlePassword"/>
               <div>
                 <button>
                   <fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromGoogleProceed"/>
                 </button>
               </div>
            </div>
          </div>
          
          <div class="siteMenuBarFriendsWidgetInviteFriendsProvider" id="siteMenuBarFriendsWidgetInviteFriendsProviderYahoo">
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderInfo">
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderImage"></div>
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderText"><fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromYahoo"/></div>
            </div>
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderOptions" style="display: none">
               <div class="siteMenuBarFriendsWidgetInviteFriendsProviderNoStoreText"><fmt:message key="generic.header.menuBarWidgetFriendsAddWillNotStoreCredentials"/></div>
               <div><fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromYahooUsername"/></div>
               <input type="text" name="yahooUsername"/>
               <div><fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromYahooPassword"/></div>
               <input type="text" name="yahooPassword"/>
               <div>
                 <button>
                   <fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromYahooProceed"/>
                 </button>
               </div>
            </div>
          </div>
          
          <div class="siteMenuBarFriendsWidgetInviteFriendsProvider" id="siteMenuBarFriendsWidgetInviteFriendsProviderFacebook">
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderInfo">
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderImage"></div>
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderText"><fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromFacebook"/></div>
            </div>
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderOptions" style="display: none">
               <div>
                 <button>
                   <fmt:message key="generic.header.menuBarWidgetFriendsAddImportFromFacebookProceed"/>
                 </button>
               </div>
            </div>
          </div>
          -->
          <div class="siteMenuBarFriendsWidgetInviteFriendsProvider" id="siteMenuBarFriendsWidgetInviteFriendsProviderEmail">
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderInfo">
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderImage"></div>
              <div class="siteMenuBarFriendsWidgetInviteFriendsProviderText"><fmt:message key="generic.header.menuBarWidgetFriendsAddByEmail"/></div>
            </div>
            <div class="siteMenuBarFriendsWidgetInviteFriendsProviderOptions" style="display: none">
               <form onsubmit="return false;">
                 <fmt:message key="generic.header.menuBarWidgetFriendsAddByEmailAddress"/>
                 <input type="text" name="emailAddress" class="required email"/>
                 <input type="submit" name="addByEmail" class="formvalid" value="<fmt:message key="generic.header.menuBarWidgetFriendsAddByEmailProceed"/>"/>
               </form>
            </div>
          </div>
        </div>
        <div id="sitemenubarfriendswidget-list">
        </div>
      </div>
    </div>
    
  
    <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetCalendarContent"></div>
    
  </div>
  
  <div id="siteMenuBarThirdColumn">
    <div class="siteMenuBarWidget siteMenuBarWidgetDisabled" id="siteMenuBarWidgetSearch"><a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetSearchLabel"/></a></div>
    <c:if test="${loggedIn}">
      <div class="siteMenuBarWidget" id="siteMenuBarWidgetAccount">
        <a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetAccountLabel"/></a>
      </div>
    </c:if>
    
    <c:if test="${!loggedIn}">
      <div class="siteMenuBarWidget" id="siteMenuBarWidgetLogin">
        <a href="${pageContext.request.contextPath}/login/"><fmt:message key="generic.header.menuBarWidgetLoginLabel"/></a>
      </div>
    </c:if>

    <div class="siteMenuBarWidget" id="siteMenuBarWidgetLocale">
      <a href="javascript:void(null);">${fn:toUpperCase(pageContext.request.locale.language)}</a>
    </div>

    <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetSearchContent">
      <span><fmt:message key="generic.header.menuBarWidgetSearchText"/></span>
      <div class="siteMenuBarWidgetSearchInputContainer">
        <input class="siteMenuBarWidgetSearchInput" type="text" name="searchText" value="consect"/>
      </div>
      <span><fmt:message key="generic.header.menuBarWidgetResultsTitle"/></span>
      <div id="siteMenuBarWidgetSearchResultsContainer">
        <ul>
          <li><a href="javascript:void(null);" class="siteMenuBarWidgetSearchResultTitle">Lorem ipsum</a>Lorem ipsum <span class="match">consec</span>tetur dolor sit amet, <span class="match">consec</span>tetur adipiscing e...</li>
          <li><a href="javascript:void(null);" class="siteMenuBarWidgetSearchResultTitle">Lorem ipsum</a>Lorem ipsum <span class="match">consec</span>tetur dolor sit amet, <span class="match">consec</span>tetur adipiscing e...</li>
          <li><a href="javascript:void(null);" class="siteMenuBarWidgetSearchResultTitle">Lorem ipsum</a>Lorem ipsum <span class="match">consec</span>tetur dolor sit amet, <span class="match">consec</span>tetur adipiscing e...</li>
        </ul>
      </div>
    </div>           
    
    <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetHelpContent">
      <ul>
        <li>
          <a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetHelpContents"/></a>
        </li>
        <li>
          <a href="javascript:void(null);"><fmt:message key="generic.header.menuBarWidgetHelpAbout"/></a>
        </li>
      </ul>
    </div> 
    
    <c:if test="${loggedIn}">
      <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetAccountContent">
        <ul>
          <li>
            <a href="${pageContext.request.contextPath}/editprofile.jsf"><fmt:message key="generic.header.menuBarWidgetAccountEditProfile"/></a>
          </li>
          <li>
            <a href="${pageContext.request.contextPath}/logout"><fmt:message key="generic.header.menuBarWidgetAccountLogout"/></a>
          </li>
        </ul>
      </div>
    </c:if>
    
    <div class="siteMenuBarWidgetContent" id="siteMenuBarWidgetLocaleContent">
      <span><fmt:message key="generic.header.menuBarWidgetLocaleSelectText"/></span>
      
      <ul>
        <li>
          <span><fmt:message key="generic.header.menuBarWidgetLocaleFinnish"/></span>
          <input type="hidden" name="locale" value="fi_FI"/>
        </li>
        <li>
          <span><fmt:message key="generic.header.menuBarWidgetLocaleEnglish"/></span>
          <input type="hidden" name="locale" value="en_US"/>
        </li>
      </ul>
    </div>
    
  </div>
</div>