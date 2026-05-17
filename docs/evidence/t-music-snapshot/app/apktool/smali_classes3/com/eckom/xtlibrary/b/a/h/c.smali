.class Lcom/eckom/xtlibrary/b/a/h/c;
.super Ljava/lang/Object;
.source "VoiceCallView.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/a/h/d;->show()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/a/h/d;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/a/h/d;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/c;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/c;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/h/d;->j(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/widget/LinearLayout;

    move-result-object v0

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setVisibility(I)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/c;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/h/d;->c(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/widget/LinearLayout;

    move-result-object v0

    invoke-virtual {v0}, Landroid/widget/LinearLayout;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    check-cast v0, Landroid/graphics/drawable/AnimationDrawable;

    .line 3
    invoke-virtual {v0}, Landroid/graphics/drawable/AnimationDrawable;->stop()V

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/c;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/h/d;->hide()V

    return-void
.end method
