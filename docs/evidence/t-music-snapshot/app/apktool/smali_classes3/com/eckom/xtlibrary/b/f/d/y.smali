.class Lcom/eckom/xtlibrary/b/f/d/y;
.super Ljava/lang/Thread;
.source "MusicIjkID3Model.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/L;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/g;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic Fn:[Lcom/eckom/xtlibrary/b/f/b/f;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/L;

.field final synthetic tk:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/L;[Lcom/eckom/xtlibrary/b/f/b/f;Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/y;->Fn:[Lcom/eckom/xtlibrary/b/f/b/f;

    iput-object p3, p0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Thread;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 18

    move-object/from16 v0, p0

    .line 1
    invoke-super/range {p0 .. p0}, Ljava/lang/Thread;->run()V

    .line 2
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    const/4 v2, 0x0

    move v3, v2

    .line 3
    :goto_0
    iget-object v4, v0, Lcom/eckom/xtlibrary/b/f/d/y;->Fn:[Lcom/eckom/xtlibrary/b/f/b/f;

    array-length v4, v4

    if-ge v3, v4, :cond_e

    .line 4
    invoke-virtual {v1}, Ljava/util/ArrayList;->clear()V

    .line 5
    iget-object v4, v0, Lcom/eckom/xtlibrary/b/f/d/y;->Fn:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v4, v4, v3

    .line 6
    iget-object v5, v4, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    .line 7
    new-instance v6, Ljava/io/File;

    invoke-direct {v6, v5}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v5, Lcom/eckom/xtlibrary/b/f/d/x;

    invoke-direct {v5, v0}, Lcom/eckom/xtlibrary/b/f/d/x;-><init>(Lcom/eckom/xtlibrary/b/f/d/y;)V

    invoke-virtual {v6, v5}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v5

    if-nez v5, :cond_0

    goto/16 :goto_6

    .line 8
    :cond_0
    array-length v6, v5

    iput v6, v4, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    const/4 v6, 0x0

    .line 9
    iget-object v7, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    const-string v8, "/mnt/sdcard"

    invoke-virtual {v7, v8}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v7

    const-string v9, "/storage/extsd"

    const-string v10, "/storage/usb"

    const/4 v11, 0x1

    if-eqz v7, :cond_1

    .line 10
    new-instance v6, Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    const/4 v7, 0x3

    invoke-direct {v6, v4, v7, v2, v11}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 11
    iput-object v8, v6, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 12
    array-length v4, v5

    invoke-virtual {v6, v4}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    move-object v4, v8

    goto :goto_2

    .line 13
    :cond_1
    iget-object v7, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v7, v10}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v7

    const/16 v12, 0x9

    if-eqz v7, :cond_2

    .line 14
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v6, v12}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v6

    .line 15
    new-instance v7, Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    const/4 v12, 0x2

    invoke-direct {v7, v4, v12, v2, v11}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 16
    iput-object v6, v7, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 17
    array-length v4, v5

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    :goto_1
    move-object v4, v6

    move-object v6, v7

    goto :goto_2

    .line 18
    :cond_2
    iget-object v7, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v7, v9}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v7

    if-eqz v7, :cond_3

    .line 19
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v6, v12}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v6

    .line 20
    new-instance v7, Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    invoke-direct {v7, v4, v11, v2, v11}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 21
    iput-object v6, v7, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 22
    array-length v4, v5

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    goto :goto_1

    :cond_3
    const-string v4, ""

    :goto_2
    move v7, v2

    .line 23
    :goto_3
    array-length v12, v5

    if-ge v7, v12, :cond_5

    .line 24
    aget-object v12, v5, v7

    invoke-virtual {v12}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v12

    .line 25
    new-instance v13, Ljava/io/File;

    invoke-direct {v13, v12}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 26
    invoke-virtual {v13}, Ljava/io/File;->exists()Z

    move-result v14

    if-eqz v14, :cond_4

    .line 27
    new-instance v14, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-virtual {v13}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v13

    invoke-direct {v14, v13, v12}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    if-eqz v6, :cond_4

    .line 28
    invoke-virtual {v6, v14}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    .line 29
    :cond_4
    invoke-virtual {v1, v12}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    add-int/lit8 v7, v7, 0x1

    goto :goto_3

    .line 30
    :cond_5
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v5, v8}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v5

    if-eqz v5, :cond_6

    .line 31
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->Tj:Ljava/util/ArrayList;

    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_4

    .line 32
    :cond_6
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v5, v10}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v5

    if-eqz v5, :cond_8

    .line 33
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->Aj:Ljava/util/LinkedHashMap;

    iget-object v7, v6, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v5, v7}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/ArrayList;

    if-nez v5, :cond_7

    .line 34
    new-instance v5, Ljava/util/ArrayList;

    invoke-direct {v5}, Ljava/util/ArrayList;-><init>()V

    .line 35
    iget-object v7, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->Aj:Ljava/util/LinkedHashMap;

    iget-object v8, v6, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v7, v8, v5}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 36
    :cond_7
    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_4

    .line 37
    :cond_8
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v5, v9}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v5

    if-eqz v5, :cond_a

    .line 38
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->Mj:Ljava/util/LinkedHashMap;

    iget-object v7, v6, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v5, v7}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/ArrayList;

    if-nez v5, :cond_9

    .line 39
    new-instance v5, Ljava/util/ArrayList;

    invoke-direct {v5}, Ljava/util/ArrayList;-><init>()V

    .line 40
    iget-object v7, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->Mj:Ljava/util/LinkedHashMap;

    iget-object v8, v6, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v7, v8, v5}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 41
    :cond_9
    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    :cond_a
    :goto_4
    const/4 v5, 0x5

    .line 42
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v6

    if-le v6, v5, :cond_b

    .line 43
    div-int/lit8 v11, v6, 0x5

    .line 44
    :cond_b
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v7, "scanMediaID3 run: threadSize="

    invoke-virtual {v5, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    const-string v7, "MusicIjkID3Model"

    invoke-static {v7, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    move v5, v2

    :goto_5
    if-ge v5, v11, :cond_d

    add-int/lit8 v7, v5, 0x1

    mul-int/lit8 v8, v7, 0x5

    if-le v8, v6, :cond_c

    move v8, v6

    :cond_c
    mul-int/lit8 v5, v5, 0x5

    .line 45
    invoke-virtual {v1, v5, v8}, Ljava/util/ArrayList;->subList(II)Ljava/util/List;

    move-result-object v16

    .line 46
    new-instance v5, Lcom/eckom/xtlibrary/b/f/a/a;

    iget-object v8, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/f/d/L;->b(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/content/Context;

    move-result-object v13

    iget-object v14, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    iget-object v8, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/f/d/L;->j(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/c/a;

    move-result-object v17

    move-object v12, v5

    move-object v15, v4

    invoke-direct/range {v12 .. v17}, Lcom/eckom/xtlibrary/b/f/a/a;-><init>(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lcom/eckom/xtlibrary/b/f/c/a;)V

    .line 47
    iget-object v8, v0, Lcom/eckom/xtlibrary/b/f/d/y;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/f/d/L;->k(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/a/c;

    move-result-object v8

    iget-object v9, v0, Lcom/eckom/xtlibrary/b/f/d/y;->tk:Ljava/lang/String;

    invoke-virtual {v8, v9, v5}, Lcom/eckom/xtlibrary/b/f/a/c;->a(Ljava/lang/String;Ljava/lang/Runnable;)V

    move v5, v7

    goto :goto_5

    :cond_d
    :goto_6
    add-int/lit8 v3, v3, 0x1

    goto/16 :goto_0

    :cond_e
    return-void
.end method
