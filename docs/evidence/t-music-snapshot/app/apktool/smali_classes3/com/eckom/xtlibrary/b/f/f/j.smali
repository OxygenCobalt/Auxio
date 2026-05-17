.class Lcom/eckom/xtlibrary/b/f/f/j;
.super Ljava/lang/Object;
.source "MusicUtils.java"

# interfaces
.implements Ljava/io/FileFilter;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/f/h$c;->doInBackground([Ljava/lang/Void;)Lcom/eckom/xtlibrary/b/f/b/g;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic Ck:Ljava/util/ArrayList;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/f/h$c;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/f/h$c;Ljava/util/ArrayList;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/j;->this$0:Lcom/eckom/xtlibrary/b/f/f/h$c;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/f/j;->Ck:Ljava/util/ArrayList;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public accept(Ljava/io/File;)Z
    .locals 2

    .line 1
    invoke-virtual {p1}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v0

    .line 2
    invoke-virtual {p1}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object p1

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/j;->Ck:Ljava/util/ArrayList;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-direct {v1, v0, p1}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {p0, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    const/4 p0, 0x1

    return p0
.end method
