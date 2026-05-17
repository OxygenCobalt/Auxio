.class public abstract Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;
.super Landroid/app/Fragment;
.source "XTFragment.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/l/a;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Landroid/app/Fragment;",
        "Lcom/eckom/xtlibrary/b/l/a;"
    }
.end annotation


# instance fields
.field protected mContext:Landroid/content/Context;

.field public mPresenter:Lcom/eckom/xtlibrary/b/g/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "TP;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Landroid/app/Fragment;-><init>()V

    return-void
.end method


# virtual methods
.method public abstract a(Landroid/view/View;)V
.end method

.method public onAttach(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Landroid/app/Fragment;->onAttach(Landroid/content/Context;)V

    .line 2
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mContext:Landroid/content/Context;

    return-void
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Landroid/app/Fragment;->onCreate(Landroid/os/Bundle;)V

    return-void
.end method

.method public onDestroy()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/g/a;->delete()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/g/a;->onDestroy()V

    .line 3
    invoke-super {p0}, Landroid/app/Fragment;->onDestroy()V

    return-void
.end method

.method public onPause()V
    .locals 0

    .line 1
    invoke-super {p0}, Landroid/app/Fragment;->onPause()V

    return-void
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-super {p0, p1, p2}, Landroid/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    .line 2
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->a(Landroid/view/View;)V

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-nez p1, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->ra()Lcom/eckom/xtlibrary/b/g/a;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/g/a;->a(Lcom/eckom/xtlibrary/b/l/a;)V

    :cond_0
    return-void
.end method

.method public abstract ra()Lcom/eckom/xtlibrary/b/g/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TP;"
        }
    .end annotation
.end method
