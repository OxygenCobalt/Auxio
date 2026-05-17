.class Lcom/eckom/xtlibrary/twproject/video/model/d;
.super Ljava/lang/Object;
.source "VideoIjkModel.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/m;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 1

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->i(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object p1

    sget v0, Lcom/eckom/xtlibrary/R$id;->img_suspension_pp:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    invoke-virtual {p1}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v0

    xor-int/lit8 v0, v0, 0x1

    invoke-virtual {p1, v0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result p1

    if-nez p1, :cond_0

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    goto :goto_0

    .line 4
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->P()V

    .line 5
    :goto_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->q(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    return-void
.end method
