package com.wcarmon.codegen.model.ast

/**
 * Useful for java, dart, c, c++
 *
 * @return ; only when terminate is true
 */
fun serializeTerminator(terminate: Boolean): String =
  if (terminate) ";"
  else ""
