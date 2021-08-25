@file:JvmName("FreeMarkerUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.Entity
import freemarker.template.DefaultListAdapter
import freemarker.template.TemplateMethodModelEx

val DISTINCT_PROTO_COLLECTION_FIELDS_METHOD = object : TemplateMethodModelEx {

  @Suppress("Unchecked_cast")
  override fun exec(arguments: MutableList<Any?>): Any {
    val listAdapter = arguments[0] as DefaultListAdapter
    val entities = listAdapter.wrappedObject as Collection<Entity>
    return getDistinctProtoCollectionFields(entities)
  }
}
