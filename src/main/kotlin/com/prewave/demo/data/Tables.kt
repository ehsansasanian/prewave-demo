package com.prewave.demo.data

import org.jooq.impl.DSL
import org.jooq.impl.DSL.primaryKey
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl

/*
* For a more robust approach, JOOQ provides code generation tools that
* automatically create these classes from the database schema.
*
* Migration Path:
* Setting up code generation in the build
* Run the generator
* Refactor repositories to use the generated classes
* Remove manual Table implementations
* */

internal object Tables {
    val EDGETable = EdgeTable()

    class EdgeTable : TableImpl<Nothing>(DSL.name("edge")) {
        val FROM_ID = createField(DSL.name("from_id"), SQLDataType.INTEGER.notNull())
        val TO_ID = createField(DSL.name("to_id"), SQLDataType.INTEGER.notNull())

        init {
            primaryKey(FROM_ID, TO_ID)
        }
    }
}
