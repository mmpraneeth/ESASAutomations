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
		<div class="slds-col slds-col-rule--right slds-p-right--large slds-size--6-of-12">
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
				  <li class="slds-accordion__list-item" >
				    <section class="slds-accordion__section" onclick="$(this).toggleClass('slds-is-open')">
				      <div class="slds-accordion__summary" >
				        <h3 class="slds-text-heading_small slds-accordion__summary-heading">
				          <button aria-controls="accordion-details-01" aria-expanded="true" class="slds-button slds-button_reset slds-accordion__summary-action">
				            <svg class="slds-accordion__summary-action-icon slds-button__icon slds-button__icon_left" aria-hidden="true">
				              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="view/salesforce-lightning-design-system-2.4.3/assets/icons/utility-sprite/svg/symbols.svg#switch" />
				            </svg>
				            <span class="slds-truncate" title="Accordion summary">Object Name</span>
				          </button>
				        </h3>				        
				      </div>
				      <div aria-hidden="false" class="slds-accordion__content" id="accordion-details-01">Accordion details - A</div>
				    </section>
				  </li>				  
				</ul>
		    </div>
		    <!-- / CARD BODY = SECTION + TABLE -->
		    <div class="slds-card__footer">
		      <a href="javascript:void(0);">View All <span class="slds-assistive-text">contacts</span></a>
		    </div>
		 
		  </article>
		</div>
		<!-- / MAIN CARD -->


	    <!-- NARROW CARD -->
	    <div class="slds-col slds-p-left--large slds-size--6-of-12">
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
				  <li class="slds-accordion__list-item" >
				    <section class="slds-accordion__section" onclick="$(this).toggleClass('slds-is-open')">
				      <div class="slds-accordion__summary" >
				        <h3 class="slds-text-heading_small slds-accordion__summary-heading">
				          <button aria-controls="accordion-details-01" aria-expanded="true" class="slds-button slds-button_reset slds-accordion__summary-action">
				            <svg class="slds-accordion__summary-action-icon slds-button__icon slds-button__icon_left" aria-hidden="true">
				              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="view/salesforce-lightning-design-system-2.4.3/assets/icons/utility-sprite/svg/symbols.svg#switch" />
				            </svg>
				            <span class="slds-truncate" title="Accordion summary">Object Name</span>
				          </button>
				        </h3>				        
				      </div>
				      <div aria-hidden="false" class="slds-accordion__content" id="accordion-details-01">Accordion details - A</div>
				    </section>
				  </li>				  
				</ul>
		    </div>
		    <!-- / CARD BODY = SECTION + TABLE -->
		    <div class="slds-card__footer">
		      <a href="javascript:void(0);">View All <span class="slds-assistive-text">contacts</span></a>
		    </div>
		 
		  </article>
	    </div>
	    <!-- / NARROW CARD -->
	  </div>
	  <!-- / RELATED LIST CARDS -->
	 </div>
    <!-- / PRIMARY CONTENT WRAPPER -->
    
    <!-- FOOTER -->
    <footer role="contentinfo" class="slds-p-around--large">
      <!-- LAYOUT GRID -->
      <div class="slds-grid slds-grid--align-spread">
        <p class="slds-col">Salesforce Lightning Design System Example</p>
        <p class="slds-col">&copy; Your Name Here</p>
      </div>
      <!-- / LAYOUT GRID -->
    </footer>
    <!-- / FOOTER -->
  </div>



</body>
</html>