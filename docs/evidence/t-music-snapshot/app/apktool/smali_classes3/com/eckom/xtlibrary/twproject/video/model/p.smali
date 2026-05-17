.class Lcom/eckom/xtlibrary/twproject/video/model/p;
.super Ljava/lang/Object;
.source "VideoModel.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/z;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/z;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/p;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 1

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/p;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->i(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object p1

    sget v0, Lcom/eckom/xtlibrary/R$id;->img_suspension_pp:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    invoke-virtual {p1}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/p;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v0

    xor-int/lit8 v0, v0, 0x1

    invoke-virtual {p1, v0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/p;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result p1

    if-nez p1, :cond_0

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/p;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->ma()V

    goto :goto_0

    .line 4
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/p;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->P()V

    .line 5
    :goto_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/p;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->q(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    return-void
.end method
