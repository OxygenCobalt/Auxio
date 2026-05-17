.class Lcom/eckom/xtlibrary/twproject/radio/utils/a;
.super Ljava/lang/Object;
.source "DBHelper.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/twproject/radio/utils/b;->a(Landroid/database/sqlite/SQLiteDatabase;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic Al:Ljava/io/BufferedReader;

.field final synthetic Bl:Ljava/io/File;

.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

.field final synthetic yl:Landroid/database/sqlite/SQLiteDatabase;

.field final synthetic zl:[Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/radio/utils/b;Landroid/database/sqlite/SQLiteDatabase;[Ljava/lang/String;Ljava/io/BufferedReader;Ljava/io/File;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    iput-object p2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->yl:Landroid/database/sqlite/SQLiteDatabase;

    iput-object p3, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->zl:[Ljava/lang/String;

    iput-object p4, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->Al:Ljava/io/BufferedReader;

    iput-object p5, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->Bl:Ljava/io/File;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 9

    const-string v0, "  22222222  "

    .line 1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->yl:Landroid/database/sqlite/SQLiteDatabase;

    invoke-virtual {v1}, Landroid/database/sqlite/SQLiteDatabase;->beginTransaction()V

    :try_start_0
    const-string v1, "INSERT INTO radiologo (pi, freq, icon) VALUES (?, ?, ?)"

    .line 2
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->yl:Landroid/database/sqlite/SQLiteDatabase;

    invoke-virtual {v2, v1}, Landroid/database/sqlite/SQLiteDatabase;->compileStatement(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;

    move-result-object v1

    .line 3
    :cond_0
    :goto_0
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->zl:[Ljava/lang/String;

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->Al:Ljava/io/BufferedReader;

    invoke-virtual {v3}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v3

    const/4 v4, 0x0

    aput-object v3, v2, v4

    if-eqz v3, :cond_1

    .line 4
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->zl:[Ljava/lang/String;

    aget-object v2, v2, v4

    const-string v3, ","

    invoke-virtual {v2, v3}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v2

    .line 5
    aget-object v3, v2, v4

    const/4 v4, 0x1

    .line 6
    aget-object v5, v2, v4

    const/4 v6, 0x2

    .line 7
    aget-object v7, v2, v6

    .line 8
    array-length v2, v2

    const/4 v8, 0x3

    if-ne v2, v8, :cond_0

    .line 9
    invoke-virtual {v1, v4, v3}, Landroid/database/sqlite/SQLiteStatement;->bindString(ILjava/lang/String;)V

    .line 10
    invoke-virtual {v1, v6, v5}, Landroid/database/sqlite/SQLiteStatement;->bindString(ILjava/lang/String;)V

    .line 11
    invoke-virtual {v1, v8, v7}, Landroid/database/sqlite/SQLiteStatement;->bindString(ILjava/lang/String;)V

    .line 12
    invoke-virtual {v1}, Landroid/database/sqlite/SQLiteStatement;->executeInsert()J

    goto :goto_0

    .line 13
    :cond_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->a(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Landroid/content/Context;

    move-result-object v1

    const-string v2, "Radio"

    const-string v3, "radio_freq_logo_data_size"

    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->Bl:Ljava/io/File;

    invoke-virtual {v4}, Ljava/io/File;->length()J

    move-result-wide v4

    invoke-static {v1, v2, v3, v4, v5}, Lcom/eckom/xtlibrary/b/j/o;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;J)V

    .line 14
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->yl:Landroid/database/sqlite/SQLiteDatabase;

    invoke-virtual {v1}, Landroid/database/sqlite/SQLiteDatabase;->setTransactionSuccessful()V
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 15
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->yl:Landroid/database/sqlite/SQLiteDatabase;

    invoke-virtual {v1}, Landroid/database/sqlite/SQLiteDatabase;->endTransaction()V

    .line 16
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v1

    if-eqz v1, :cond_2

    .line 17
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v1

    invoke-interface {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;->O()V

    .line 18
    :cond_2
    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    .line 19
    :try_start_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->Al:Ljava/io/BufferedReader;

    invoke-virtual {p0}, Ljava/io/BufferedReader;->close()V
    :try_end_1
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_1

    goto :goto_1

    :catchall_0
    move-exception v1

    goto :goto_2

    :catch_0
    move-exception v1

    .line 20
    :try_start_2
    invoke-virtual {v1}, Ljava/io/IOException;->printStackTrace()V

    .line 21
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v1

    if-eqz v1, :cond_3

    .line 22
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v1

    invoke-interface {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;->Q()V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    .line 23
    :cond_3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->yl:Landroid/database/sqlite/SQLiteDatabase;

    invoke-virtual {v1}, Landroid/database/sqlite/SQLiteDatabase;->endTransaction()V

    .line 24
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v1

    if-eqz v1, :cond_4

    .line 25
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v1

    invoke-interface {v1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;->O()V

    .line 26
    :cond_4
    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    .line 27
    :try_start_3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->Al:Ljava/io/BufferedReader;

    invoke-virtual {p0}, Ljava/io/BufferedReader;->close()V
    :try_end_3
    .catch Ljava/io/IOException; {:try_start_3 .. :try_end_3} :catch_1

    goto :goto_1

    :catch_1
    move-exception p0

    .line 28
    invoke-virtual {p0}, Ljava/io/IOException;->printStackTrace()V

    :goto_1
    return-void

    .line 29
    :goto_2
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->yl:Landroid/database/sqlite/SQLiteDatabase;

    invoke-virtual {v2}, Landroid/database/sqlite/SQLiteDatabase;->endTransaction()V

    .line 30
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v2

    if-eqz v2, :cond_5

    .line 31
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->this$0:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->b(Lcom/eckom/xtlibrary/twproject/radio/utils/b;)Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    move-result-object v2

    invoke-interface {v2}, Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;->O()V

    .line 32
    :cond_5
    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    .line 33
    :try_start_4
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/a;->Al:Ljava/io/BufferedReader;

    invoke-virtual {p0}, Ljava/io/BufferedReader;->close()V
    :try_end_4
    .catch Ljava/io/IOException; {:try_start_4 .. :try_end_4} :catch_2

    goto :goto_3

    :catch_2
    move-exception p0

    .line 34
    invoke-virtual {p0}, Ljava/io/IOException;->printStackTrace()V

    .line 35
    :goto_3
    throw v1
.end method
