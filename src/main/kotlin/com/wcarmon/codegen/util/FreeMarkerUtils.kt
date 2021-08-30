@file:JvmName("FreeMarkerUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.Entity
import freemarker.template.DefaultListAdapter
import freemarker.template.TemplateMethodModelEx

//TODO: consider moving to entity.protoView
@Suppress("Unchecked_cast")
val DISTINCT_PROTO_COLLECTION_FIELDS_METHOD = TemplateMethodModelEx { arguments ->
  val listAdapter = arguments[0] as DefaultListAdapter
  val entities = listAdapter.wrappedObject as Collection<Entity>
  getDistinctProtoCollectionFields(entities)
}
