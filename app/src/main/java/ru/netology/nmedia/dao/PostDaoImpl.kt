package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post


class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    companion object {
        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKES_COUNT} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARES_COUNT} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIEWS_COUNT} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_IMG_LINK} INTEGER DEFAULT NULL,
            ${PostColumns.COLUMN_VIDEO_PREVIEW_LINK} INTEGER DEFAULT NULL,
            ${PostColumns.COLUMN_VIDEO_LINK} TEXT DEFAULT NULL
        );
        """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_LIKES_COUNT = "likesCount"
        const val COLUMN_SHARES_COUNT = "sharesCount"
        const val COLUMN_VIEWS_COUNT = "viewsCount"
        const val COLUMN_IMG_LINK = "imgLink"
        const val COLUMN_VIDEO_PREVIEW_LINK = "videoPreviewLink"
        const val COLUMN_VIDEO_LINK = "videoLink"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKED_BY_ME,
            COLUMN_LIKES_COUNT,
            COLUMN_SHARES_COUNT,
            COLUMN_VIEWS_COUNT,
            COLUMN_IMG_LINK,
            COLUMN_VIDEO_PREVIEW_LINK,
            COLUMN_VIDEO_LINK
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            put(PostColumns.COLUMN_AUTHOR, "Me")
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, "Now")
        }
        val id = db.replace(PostColumns.TABLE, null, values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
            UPDATE posts SET
                likesCount = likesCount + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
            WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
            UPDATE posts SET
                sharesCount = sharesCount + 1
            WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    private fun map(cursor: Cursor) : Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                likesCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES_COUNT)),
                sharesCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARES_COUNT)),
                viewsCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIEWS_COUNT)),
                imgLink = try {
                    getInt(getColumnIndexOrThrow(PostColumns.COLUMN_IMG_LINK))
                } catch (e: IllegalArgumentException) {
                    null
                },
                videoPreviewLink = try {
                    getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_PREVIEW_LINK))
                } catch (e: IllegalArgumentException) {
                    null
                },
                videoLink = try {
                    getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_LINK))
                } catch (e: IllegalArgumentException) {
                    null
                }
            )
        }
    }
}