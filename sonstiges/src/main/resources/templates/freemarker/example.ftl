<#import "macro.ftl" as mymacro>

<#assign this="Freemarker">
${this} is great!

<#list names as name>
	${name} is great!
</#list>

<#assign condition = true>
<#if condition>
  The condition is true!
<#else>
  The condition is false!
</#if>  

<@mymacro.printMethods entity=Math />

PI is ${PI}

<#--
some comment...
-->