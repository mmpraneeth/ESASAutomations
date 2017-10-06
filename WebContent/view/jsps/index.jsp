<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Data Fill Rate</title>

<spring:url value="/resources/core/css/hello.css" var="coreCss" />
<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link rel="stylesheet" type="text/css" href="view/salesforce-lightning-design-system-2.4.3/assets/styles/salesforce-lightning-design-system.css" />
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
	          <h1 class="slds-page-header__title slds-m-right--small slds-align-middle slds-truncate" title="SLDS Inc.">Data Fill Rate</h1>
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
		          <svg aria-hidden="true" class="slds-icon slds-icon-standard-contact slds-icon--small">
		            <use xlink:href="{!URLFOR($Asset.SLDS, 'assets/icons/standard-sprite/svg/symbols.svg#contact')}"></use>
		          </svg>
		        </div>
		        <div class="slds-media__body slds-truncate">
		          <a href="javascript:void(0);" class="slds-text-link--reset">
		            <span class="slds-text-heading--small">ORG 62</span>
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
				    <section class="slds-accordion__section" onclick="$(this).toggleClass('slds-is-open')">
				      <div class="slds-accordion__summary" >
				        <h3 class="slds-text-heading_small slds-accordion__summary-heading">
				          <button aria-controls="accordion-details-01" aria-expanded="true" class="slds-button slds-section__title-action ">
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
						        <div class="slds-truncate" title="Opportunity Name">Field Name</div>
						      </th>
						      <th scope="col">
						        <div class="slds-truncate" title="Account Name">Fill Rate</div>
						      </th>
						     </tr>
						   </thead>
						   <tbody>
						   <c:forEach items="${record.value}" var="fields">
						    <tr>
						      <th scope="row" data-label="Opportunity Name">
						        <div class="slds-truncate" title="Cloudhub"><a href="javascript:void(0);">${fields.fieldName}</a></div>
						      </th>
						      <td data-label="Account Name">
						        <div class="slds-truncate" title="Cloudhub">
						         <c:choose>
						        	<c:when test="${fields.fillRate >= 90}">
						        		<font color= "red">
						        	</c:when>
						        	<c:when test="${fields.fillRate >= 80}">
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
			      	<a href="javascript:void(0);">View All <span class="slds-assistive-text">contacts</span></a>
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
		          <svg aria-hidden="true" class="slds-icon slds-icon-standard-contact slds-icon--small">
		            <use xlink:href="{!URLFOR($Asset.SLDS, 'assets/icons/standard-sprite/svg/symbols.svg#contact')}"></use>
		          </svg>
		        </div>
		        <div class="slds-media__body slds-truncate">
		          <a href="javascript:void(0);" class="slds-text-link--reset">
		            <span class="slds-text-heading--small">SUPPORTFORCE</span>
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
				    <section class="slds-accordion__section" onclick="$(this).toggleClass('slds-is-open')">
				      <div class="slds-accordion__summary" >
				        <h3 class="slds-text-heading_small slds-accordion__summary-heading">
				          <button aria-controls="accordion-details-01" aria-expanded="true" class="slds-button slds-section__title-action">
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
						        <div class="slds-truncate" title="Opportunity Name">Field Name</div>
						      </th>
						      <th scope="col">
						        <div class="slds-truncate" title="Account Name">Fill Rate</div>
						      </th>
						     </tr>
						   </thead>
						   <tbody>		
						   <c:forEach items="${record.value}" var="fields">
						    <tr>
						      <th scope="row" data-label="Opportunity Name">
						        <div class="slds-truncate" title="Cloudhub"><a href="javascript:void(0);">${fields.fieldName}</a></div>
						      </th>
						      <td data-label="Account Name">
						         <c:choose>
						        	<c:when test="${fields.fillRate >= 90}">
						        		<font color= "red">
						        	</c:when>
						        	<c:when test="${fields.fillRate >= 80}">
						        		<font color= "#f4a641">
						        	</c:when>
						        	<c:otherwise>
						        		<font>
						        	</c:otherwise>
						        </c:choose>
						        	
						        	${fields.fillRate} % <c:out value="${org62.length}"></c:out>
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