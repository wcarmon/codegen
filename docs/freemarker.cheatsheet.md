# Overview
- Key differences from Velocity


# Template
- See `freemarker.template.Template`
- See `org.apache.velocity.Template`


# Converting from Velocity
|Velocity                     |Freemarker|
|---                          |---|
|`##`                         | `<#--`|
|`#else`                      |`<#else>`|
|`#foreach(x in y)  ... #end` |`<#list y as x>...</#list>`|
|`#if($b) ... #end`           |`<#if b></#if>`|
|`$foreach.hasNext`           |`theVariable?has_next}`|
|`$foreach.last`              |`theVariable?is_last}`|
|`.size()}`                   |`?size}`|
|`myCollection.isEmpty()`     |`myCollection?has_content`|


# Cons from Velocity
- invoking method on object in template is harder than velocity (properties are easy)
- All property access requires ${} syntax (no $variable)


# Pros over Velocity
- Can write macros in either template language or a jvm language
- Has has better template composition mechanisms than Velocity
- Macros don't require #set directive
- Passing macro params is more natural
- closing tag on </#list> and </#if> are unambiguous


# Common to Velocity & Freemarker
- Intellij formatter breaks template
- Supports typesafe completion via implicits file
