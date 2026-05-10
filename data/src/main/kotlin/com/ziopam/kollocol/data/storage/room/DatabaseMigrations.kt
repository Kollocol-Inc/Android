package com.ziopam.kollocol.data.storage.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE quiz_instances ADD COLUMN status TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS templates (
                id TEXT PRIMARY KEY NOT NULL,
                user_id TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                quiz_type TEXT NOT NULL,
                settings TEXT,
                questions TEXT NOT NULL
            )
        """.trimIndent())
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE templates ADD COLUMN total_questions INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE templates ADD COLUMN total_time INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `group_details` (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                avatarUrl TEXT,
                memberCount INTEGER NOT NULL,
                pendingCount INTEGER NOT NULL,
                ownerId TEXT NOT NULL,
                members TEXT NOT NULL,
                invitedUsers TEXT NOT NULL
            )
        """.trimIndent())
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `groups` (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                avatarUrl TEXT,
                memberCount INTEGER NOT NULL,
                pendingCount INTEGER NOT NULL,
                ownerId TEXT NOT NULL,
                membershipType TEXT NOT NULL
            )
        """.trimIndent())
    }
}