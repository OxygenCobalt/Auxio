.class Lcom/eckom/xtlibrary/b/f/d/i;
.super Ljava/lang/Object;
.source "MusicID3Model.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnCompletionListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/t;->reset()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/t;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/t;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/i;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onCompletion(Landroid/media/MediaPlayer;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/i;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->pb()V

    return-void
.end method
