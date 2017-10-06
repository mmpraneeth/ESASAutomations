<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Field Scan - Data Fill Rate</title>

<spring:url value="/resources/core/css/hello.css" var="coreCss" />
<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link rel="stylesheet" type="text/css" href="view/salesforce-lightning-design-system-2.4.3/assets/styles/salesforce-lightning-design-system.css" />
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="//c2.sfdcstatic.com/etc/designs/sfdc-www/en_us/favicon.ico">
<script
  src="https://code.jquery.com/jquery-3.2.1.js"
  integrity="sha256-DZAnKJ/6XZ9si04Hgrsxu/8s717jcIzLy3oi35EouyE="
  crossorigin="anonymous"></script>
</head>
<body>

<div class="slds-scope">
    <!-- MASTHEAD -->
    <p class="slds-text-heading--label slds-m-bottom--small"></p>
    <!-- / MASTHEAD -->
    

	<!-- PAGE HEADER -->
	<div class="slds-page-header">
	  <!-- PAGE HEADER TOP ROW -->
	  <div class="slds-grid">
	    <!-- PAGE HEADER / ROW 1 / COLUMN 1 -->
	    <div class="slds-col slds-has-flexi-truncate">
	      <!-- HEADING AREA -->
	      <!-- MEDIA OBJECT = FIGURE + BODY -->
	      <div class="slds-media slds-no-space slds-grow">
	        <div class="slds-media__figure">
	        </div>
	        <div class="slds-media__body">
	          <p class="slds-text-title--caps slds-line-height--reset"></p>
	           <img src="https://www.google.com/a/salesforce.com/images/logo.gif?alpha=1&amp;service=google_default" style="width:15%%;height:50px;float:left;">
	             <img src="https://www.vectorlogo.zone/logos/heroku/heroku-card.png" style="width:15%%;height:50px;float:right;">
	           <h1 class="slds-page-header__title slds-m-right--small slds-align-middle slds-truncate"  title="SLDS Inc." style="text-align: center;font-size:30px;">Field Data Storage</h1>
	        </div>
	      </div>
	      <!-- / MEDIA OBJECT -->
	      <!-- HEADING AREA -->
	    </div>
	    <!-- / PAGE HEADER / ROW 1 / COLUMN 1 -->
	    
	    <!-- PAGE HEADER / ROW 1 / COLUMN 2 -->
	    	<!-- For buttons -->
	    <!-- / PAGE HEADER / ROW 1 / COLUMN 2 -->
	  </div>
	  <!-- / PAGE HEADER TOP ROW -->
	  <!-- PAGE HEADER DETAIL ROW -->
	  <!-- / PAGE HEADER DETAIL ROW -->
	 
	</div>
	<!-- / PAGE HEADER -->
	
 
    <!-- PRIMARY CONTENT WRAPPER -->
    <div class="myapp">
	    <!-- RELATED LIST CARDS-->
	  <div class="slds-grid slds-m-top--large">
	    <!-- MAIN CARD -->
		<div class="slds-col slds-col-rule--right slds-p-left--large slds-p-right--large slds-size--6-of-12">
		   <article class="slds-card">
		    <div class="slds-card__header slds-grid">
		      <header class="slds-media slds-media--center slds-has-flexi-truncate">
	 			<div class="slds-media__figure">
		        <span class="slds-icon_container slds-icon-standard-bot">
		          <svg aria-hidden="true" class="slds-icon slds-icon--small" id="case">
		            <use xlink:href="{!URLFOR($Asset.SLDS, 'assets/icons/standard-sprite/svg/symbols.svg#bot')}"></use>
		          <svg viewBox="0 0 24 24" id="bot"><title/><path d="M11.9 6.2c1.7 0 3.1 1.4 3.1 3.1v.8c-1-.1-2.1-.2-3.1-.2s-2.1.1-3.1.2v-.8c0-1.7 1.4-3.1 3.1-3.1zm5.7 9.1l.3-2.7c.7.1 1.2.7 1.2 1.3 0 .8-.7 1.4-1.5 1.4zm-11.4 0c-.8 0-1.5-.6-1.5-1.4 0-.7.5-1.2 1.2-1.3l.3 2.7zm10.3-4.5c-1.6-.2-3.1-.3-4.6-.3s-3 .1-4.5.3c-.6 0-.9.5-.9 1l.5 4.7c.1.4.4.8.8.8 1.4.2 2.8.2 4.2.2s2.7 0 4.1-.2c.4 0 .7-.4.8-.8l.5-4.7c0-.5-.4-1-.9-1zM9.3 15c-.4 0-.7-.4-.7-.9s.3-.9.7-.9.6.4.6.9-.3.9-.6.9zm4 1l-.1.1s0 .1-.1.1h-2.4l-.1-.1v-.5c0-.1 0-.2.1-.2h.1c.1 0 .1.1.1.2v.2h2v-.2c0-.1.1-.2.2-.2s.2.1.2.2v.4zm1.2-1c-.3 0-.6-.4-.6-.9s.3-.9.6-.9.7.4.7.9-.3.9-.7.9z"/></path></symbol></svg>
		          </svg>
		        <span class="slds-assistive-text"></span>
		        </div>
		        <div class="slds-media__body slds-truncate">
		          <a href="javascript:void(0);" class="slds-text-link--reset">
		            <span class="slds-text-heading--small">Sandbox-1</span>
		          </a>
		        </div>
		      </header>
		    </div>
		    <!-- CARD BODY = TABLE -->
		    <div class="slds-card__body">		    
		      <ul class="slds-accordion">
		      	<c:forEach items="#{org62}" var="record" varStatus="loop">
		      	<c:set var="showFooter1" value="${loop.index}"/>
				  <li class="slds-accordion__list-item" >
				    <section class="slds-accordion__section" id="sfdc1_${loop.index}">
				      <div class="slds-accordion__summary" >
				        <h3 class="slds-text-heading_small slds-accordion__summary-heading">
				          <button aria-controls="accordion-details-01" aria-expanded="true" class="slds-button slds-section__title-action " onclick="$('#sfdc1_${loop.index}').toggleClass('slds-is-open')">
				            <svg class="slds-accordion__summary-action-icon slds-button__icon slds-button__icon_left" aria-hidden="true">
				              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="view/salesforce-lightning-design-system-2.4.3/assets/icons/utility-sprite/svg/symbols.svg#switch" />
				            </svg>
				            <span class="slds-truncate" title="Accordion summary">${record.key}</span>
				          </button>
				        </h3>				        
				      </div>
				      <div aria-hidden="false" class="slds-accordion__content" id="accordion-details-01">
				      	<table class="slds-table slds-table_bordered slds-table_cell-buffer">
						  <thead>
						    <tr class="slds-text-title_caps">
						      <th scope="col">
						        <div class="slds-truncate" title="Field Name">Field Name</div>
						      </th>
						      <th scope="col">
						        <div class="slds-truncate" title="Fill Rate">Fill Rate</div>
						      </th>
						     </tr>
						   </thead>
						   <tbody>
						   <c:forEach items="${record.value}" var="fields">
						    <tr>
						      <th scope="row" data-label="Field Name">
						        <div class="slds-truncate" title="${fields.fieldName}"><a href="javascript:void(0);">${fields.fieldName}</a></div>
						      </th>
						      <td data-label="Field Name">
						        <div class="slds-truncate" title="${fields.fieldName}">
						         <c:choose>
						        	<c:when test="${fields.fillRate == 0}">
						        		<font color= "red">
						        	</c:when>
						        	<c:when test="${fields.fillRate < 20}">
						        		<font color= "#f4a641">
						        	</c:when>
						        	<c:otherwise>
						        		<font>
						        	</c:otherwise>
						        </c:choose>
						        	
						        	${fields.fillRate} %
						        	</font>
						        </div>
						      </td>
						     </tr>
						     </c:forEach>
						   </tbody>
						 </table>
					  </div>
				    </section>
				  </li>
				  </c:forEach>				  
				</ul>
		    </div>
		    <!-- / CARD BODY = SECTION + TABLE -->
		    <c:if test="${showFooter2 > 5}">
			    <div class="slds-card__footer">
			      	<a href="javascript:void(0);">View All </a>
			    </div>
		 	</c:if>
		  </article>
		</div>
		<!-- / MAIN CARD -->


	    <!-- NARROW CARD -->
	    <div class="slds-col slds-p-left--large slds-p-right--large slds-size--6-of-12">
	      <article class="slds-card">
		    <div class="slds-card__header slds-grid">
		      <header class="slds-media slds-media--center slds-has-flexi-truncate">
				 <div class="slds-media__figure">
		        <span class="slds-icon_container slds-icon-standard-bot">
		          <svg aria-hidden="true" class="slds-icon slds-icon--small" id="case">
		            <use xlink:href="{!URLFOR($Asset.SLDS, 'assets/icons/standard-sprite/svg/symbols.svg#bot')}"></use>
		          <svg viewBox="0 0 24 24" id="bot"><title/><path d="M11.9 6.2c1.7 0 3.1 1.4 3.1 3.1v.8c-1-.1-2.1-.2-3.1-.2s-2.1.1-3.1.2v-.8c0-1.7 1.4-3.1 3.1-3.1zm5.7 9.1l.3-2.7c.7.1 1.2.7 1.2 1.3 0 .8-.7 1.4-1.5 1.4zm-11.4 0c-.8 0-1.5-.6-1.5-1.4 0-.7.5-1.2 1.2-1.3l.3 2.7zm10.3-4.5c-1.6-.2-3.1-.3-4.6-.3s-3 .1-4.5.3c-.6 0-.9.5-.9 1l.5 4.7c.1.4.4.8.8.8 1.4.2 2.8.2 4.2.2s2.7 0 4.1-.2c.4 0 .7-.4.8-.8l.5-4.7c0-.5-.4-1-.9-1zM9.3 15c-.4 0-.7-.4-.7-.9s.3-.9.7-.9.6.4.6.9-.3.9-.6.9zm4 1l-.1.1s0 .1-.1.1h-2.4l-.1-.1v-.5c0-.1 0-.2.1-.2h.1c.1 0 .1.1.1.2v.2h2v-.2c0-.1.1-.2.2-.2s.2.1.2.2v.4zm1.2-1c-.3 0-.6-.4-.6-.9s.3-.9.6-.9.7.4.7.9-.3.9-.7.9z"/></path></symbol></svg>
		          </svg>
		        <span class="slds-assistive-text"></span>
		        </div>
		        <div class="slds-media__body slds-truncate">
		          <a href="javascript:void(0);" class="slds-text-link--reset">
		            <span class="slds-text-heading--small">Sandbox-2</span>
		          </a>
		        </div>
		      </header>
		    </div>
		    <!-- CARD BODY = TABLE -->
		    <div class="slds-card__body">
		      <ul class="slds-accordion">
				  <c:forEach items="#{supportforce}" var="record" varStatus="loop">	
				  <c:set var="showFooter2" value="${loop.index}"/>			  
				  <li class="slds-accordion__list-item" >
				    <section class="slds-accordion__section" id="sfdc2_${loop.index}">
				      <div class="slds-accordion__summary" >
				        <h3 class="slds-text-heading_small slds-accordion__summary-heading">
				          <button aria-controls="accordion-details-01" aria-expanded="true" class="slds-button slds-section__title-action" onclick="$('#sfdc2_${loop.index}').toggleClass('slds-is-open')">
				            <svg class="slds-accordion__summary-action-icon slds-button__icon slds-button__icon_left" aria-hidden="true">
				              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="view/salesforce-lightning-design-system-2.4.3/assets/icons/utility-sprite/svg/symbols.svg#switch" />
				            </svg>
				            <span class="slds-truncate" title="Accordion summary">${record.key}</span>
				          </button>
				        </h3>				        
				      </div>
				      <div aria-hidden="false" class="slds-accordion__content" id="accordion-details-01">
				      	<table class="slds-table slds-table_bordered slds-table_cell-buffer">
						  <thead>
						    <tr class="slds-text-title_caps">
						      <th scope="col">
						        <div class="slds-truncate" title="Field Name">Field Name</div>
						      </th>
						      <th scope="col">
						        <div class="slds-truncate" title="Fill Rate">Fill Rate</div>
						      </th>
						     </tr>
						   </thead>
						   <tbody>		
						   <c:forEach items="${record.value}" var="fields">
						    <tr>
						      <th scope="row" data-label="Field Name">
						        <div class="slds-truncate" title="${fields.fieldName}"><a href="javascript:void(0);">${fields.fieldName}</a></div>
						      </th>
						      <td data-label="Field Name">
						         <c:choose>
						        	<c:when test="${fields.fillRate == 0}">
						        		<font color= "red">
						        	</c:when>
						        	<c:when test="${fields.fillRate < 20}">
						        		<font color= "#f4a641">
						        	</c:when>
						        	<c:otherwise>
						        		<font>
						        	</c:otherwise>
						        </c:choose>
						        	
						        	${fields.fillRate} %
						        	</font>
						      </td>
						     </tr>
						     </c:forEach>
						   </tbody>
						 </table>
					  </div>
				    </section>
				  </li>
				  </c:forEach>			  
				</ul>
		    </div>
		    <!-- / CARD BODY = SECTION + TABLE -->
		    <c:if test="${showFooter2 > 5}">
			    <div class="slds-card__footer">
			      	<a href="javascript:void(0);">View All <span class="slds-assistive-text">contacts</span></a>
			    </div>
		 	</c:if>		 
		  </article>
	    </div>
	    <!-- / NARROW CARD -->
	    
	  </div>
	  <!-- / RELATED LIST CARDS -->
	 </div>
    <!-- / PRIMARY CONTENT WRAPPER -->
    
    <!-- FOOTER -->
   
    <footer role="contentinfo" class="slds-p-around--large">
      <!-- LAYOUT GRID 
      <div class="slds-grid slds-grid--align-spread">
        <p class="slds-col">
        	Salesforce Lightning Design System Example
        	<button class="slds-button slds-button_neutral" value="ORG 62">ORG 62
        	<div class="slds-col slds-p-left--small slds-size--2-of-12" style="height:200px; width:200px;">
	    	</div>
	    	</button>
	    	
        </p>
        <p class="slds-col">&copy; Your Name Here</p>
         -->
      </div>
     
      <!--
      <div class="demo-only" style="height: 640px;">
		  <section role="dialog" tabindex="-1" aria-labelledby="modal-heading-01" aria-modal="true" aria-describedby="modal-content-id-1" class="slds-modal slds-fade-in-open">
		    <div class="slds-modal__container">
		      <header class="slds-modal__header">
		        <button class="slds-button slds-button_icon slds-modal__close slds-button_icon-inverse" title="Close">
		          <svg class="slds-button__icon slds-button__icon_large" aria-hidden="true">
		            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="/assets/icons/utility-sprite/svg/symbols.svg#close" />
		          </svg>
		          <span class="slds-assistive-text">Close</span>
		        </button>
		        <h2 id="modal-heading-01" class="slds-text-heading_medium slds-hyphenate">Modal Header</h2>
		      </header>
		      <div class="slds-modal__content slds-p-around_medium" id="modal-content-id-1">
		        <p>Sit nulla est ex deserunt exercitation anim occaecat. Nostrud ullamco deserunt aute id consequat veniam incididunt duis in sint irure nisi. Mollit officia cillum Lorem ullamco minim nostrud elit officia tempor esse quis. Cillum sunt ad dolore
		          quis aute consequat ipsum magna exercitation reprehenderit magna. Tempor cupidatat consequat elit dolor adipisicing.</p>
		        <p>Dolor eiusmod sunt ex incididunt cillum quis nostrud velit duis sit officia. Lorem aliqua enim laboris do dolor eiusmod officia. Mollit incididunt nisi consectetur esse laborum eiusmod pariatur proident. Eiusmod et adipisicing culpa deserunt nostrud
		          ad veniam nulla aute est. Labore esse esse cupidatat amet velit id elit consequat minim ullamco mollit enim excepteur ea.</p>
		      </div>
		      <footer class="slds-modal__footer">
		        <button class="slds-button slds-button_neutral">Cancel</button>
		        <button class="slds-button slds-button_brand">Save</button>
		      </footer>
		    </div>
		  </section>
		  <div class="slds-backdrop slds-backdrop_open"></div>
		</div> 
       -->
      <!-- / LAYOUT GRID -->
    </footer>
    <!-- / FOOTER -->
  </div>



</body>
</html>
