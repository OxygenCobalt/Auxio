.class Lcom/eckom/xtlibrary/twproject/video/utils/k;
.super Ljava/lang/Object;
.source "TWVideo.java"

# interfaces
.implements Ljava/io/FileFilter;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/utils/l;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/utils/l;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/k;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public accept(Ljava/io/File;)Z
    .locals 5

    .line 1
    invoke-virtual {p1}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v0

    sget-object v1, Ljava/util/Locale;->ENGLISH:Ljava/util/Locale;

    invoke-virtual {v0, v1}, Ljava/lang/String;->toUpperCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object v0

    .line 2
    sget-boolean v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Hd:Z

    const-string v2, "."

    const/4 v3, 0x1

    const/4 v4, 0x0

    if-eqz v1, :cond_1

    .line 3
    invoke-virtual {p1}, Ljava/io/File;->isFile()Z

    move-result p0

    if-eqz p0, :cond_0

    invoke-virtual {v0, v2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result p0

    if-nez p0, :cond_0

    .line 4
    invoke-static {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->isVideo(Ljava/lang/String;)Z

    move-result p0

    if-eqz p0, :cond_0

    return v3

    :cond_0
    return v4

    .line 5
    :cond_1
    invoke-virtual {p1}, Ljava/io/File;->isFile()Z

    move-result p1

    if-eqz p1, :cond_3

    invoke-virtual {v0, v2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result p1

    if-nez p1, :cond_3

    move p1, v4

    .line 6
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/k;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Od:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    if-ge p1, v1, :cond_3

    .line 7
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/k;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Od:Ljava/util/List;

    invoke-interface {v1, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->endsWith(Ljava/lang/String;)Z

    move-result v1

    if-eqz v1, :cond_2

    return v3

    :cond_2
    add-int/lit8 p1, p1, 0x1

    goto :goto_0

    :cond_3
    return v4
.end method
