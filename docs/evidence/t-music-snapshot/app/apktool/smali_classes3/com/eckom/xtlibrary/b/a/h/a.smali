.class Lcom/eckom/xtlibrary/b/a/h/a;
.super Ljava/lang/Object;
.source "VoiceCallView.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/a/h/d;-><init>(Landroid/content/Context;Lcom/eckom/xtlibrary/b/a/d/h;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic qh:Lcom/eckom/xtlibrary/b/a/d/h;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/a/h/d;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/a/h/d;Lcom/eckom/xtlibrary/b/a/d/h;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/a;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/a/h/a;->qh:Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 1

    const-string p1, "VoiceCallView"

    const-string v0, "Downey:onClick: --------------"

    .line 1
    invoke-static {p1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/a;->qh:Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->sb()V

    return-void
.end method
