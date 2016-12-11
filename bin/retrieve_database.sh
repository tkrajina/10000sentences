echo Make sure the app was run in debug mode and android.permission.WRITE_EXTERNAL_STORAGE was enabled
adb pull /sdcard/debug_10000sentences.db
sqlite3 debug_10000sentences.db
