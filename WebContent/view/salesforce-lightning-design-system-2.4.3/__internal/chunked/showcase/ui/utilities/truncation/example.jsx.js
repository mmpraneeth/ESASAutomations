var SLDS=SLDS||{};SLDS["__internal/chunked/showcase/ui/utilities/truncation/example.jsx.js"]=webpackJsonpSLDS___internal_chunked_showcase([5,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139],{0:function(e,t){e.exports=React},153:function(e,t,a){"use strict";function l(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0}),t.examples=void 0;var n=l(a(0)),i=l(a(1)),s=function(e){return n.default.createElement("li",{className:(0,i.default)("slds-list__item slds-m-right_large slds-grid",e.className)},n.default.createElement("span",null,"To:"),n.default.createElement("span",{className:"slds-m-left_xx-small slds-truncate",title:e.title},e.children),n.default.createElement("span",{className:"slds-m-left_xx-small slds-no-flex"}," + 44 more"))},d=function(e){return n.default.createElement("ul",null,n.default.createElement(s,{title:"Lei Chan",className:"slds-truncate_container_"+e.width},n.default.createElement("a",{href:"javascript:void(0);"},"Lei Chan")),n.default.createElement(s,{title:"Lei Chan with Long Name",className:"slds-truncate_container_"+e.width},n.default.createElement("a",{href:"javascript:void(0);"},"Lei Chan with Long Name")),n.default.createElement(s,{title:"Lei Chan with Long Name that might go on for quite some distance futher than you might expect",className:"slds-truncate_container_"+e.width},n.default.createElement("a",{href:"javascript:void(0);"},"Lei Chan with Long Name that might go on for quite some distance futher than you might expect")))};t.examples=[{id:"fluid",label:"Fluid",element:n.default.createElement("div",{className:"slds-size_1-of-2"},n.default.createElement("p",{className:"slds-truncate",title:"Long text field with many lines and truncation will look like this. Even though the text might go on for ages and ages."},"Long text field with many lines and truncation will look like this. Even though the text might go on for ages and ages."))},{id:"25%",label:"Max-width 25%",element:n.default.createElement(d,{width:"25"})},{id:"33%",label:"Max-width 33%",element:n.default.createElement(d,{width:"33"})},{id:"50%",label:"Max-width 50%",element:n.default.createElement(d,{width:"50"})},{id:"66%",label:"Max-width 66%",element:n.default.createElement(d,{width:"66"})},{id:"75%",label:"Max-width 75%",element:n.default.createElement(d,{width:"75"})}]}},[153]);