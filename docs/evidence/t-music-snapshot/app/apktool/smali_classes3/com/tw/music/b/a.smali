.class public Lcom/tw/music/b/a;
.super Ljava/lang/Object;
.source "MusicInfo.java"


# instance fields
.field private Am:Ljava/lang/String;

.field private Bm:Ljava/lang/String;

.field private Cm:Ljava/lang/String;

.field private Dm:Landroid/graphics/Bitmap;

.field private Em:Z

.field private Fm:I

.field private Vh:Ljava/lang/String;

.field private duration:I

.field private index:I

.field private mCurrentPosition:I

.field private oj:Z


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/tw/music/b/a;->mCurrentPosition:I

    .line 3
    iput v0, p0, Lcom/tw/music/b/a;->Fm:I

    return-void
.end method


# virtual methods
.method public H(Z)V
    .locals 0

    .line 1
    iput-boolean p1, p0, Lcom/tw/music/b/a;->Em:Z

    return-void
.end method

.method public Nb()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/b/a;->Bm:Ljava/lang/String;

    return-object p0
.end method

.method public a(Landroid/graphics/Bitmap;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/b/a;->Dm:Landroid/graphics/Bitmap;

    return-void
.end method

.method public ed()Landroid/graphics/Bitmap;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/b/a;->Dm:Landroid/graphics/Bitmap;

    return-object p0
.end method

.method public fd()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/b/a;->Cm:Ljava/lang/String;

    return-object p0
.end method

.method public gd()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/b/a;->Vh:Ljava/lang/String;

    return-object p0
.end method

.method public getCurrentPosition()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/b/a;->mCurrentPosition:I

    return p0
.end method

.method public getDuration()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/b/a;->duration:I

    return p0
.end method

.method public hd()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/b/a;->Fm:I

    return p0
.end method

.method public isPlaying()Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/tw/music/b/a;->Em:Z

    return p0
.end method

.method public jd()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/b/a;->Am:Ljava/lang/String;

    return-object p0
.end method

.method public kb(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/b/a;->Cm:Ljava/lang/String;

    return-void
.end method

.method public lb(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/b/a;->Vh:Ljava/lang/String;

    return-void
.end method

.method public mb(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/b/a;->Am:Ljava/lang/String;

    return-void
.end method

.method public nb(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/b/a;->Bm:Ljava/lang/String;

    return-void
.end method

.method public setDuration(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/b/a;->duration:I

    return-void
.end method

.method public setIndex(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/b/a;->index:I

    return-void
.end method

.method public toString()Ljava/lang/String;
    .locals 3

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "MusicInfo{singerName=\'"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/tw/music/b/a;->Am:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/16 v1, 0x27

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    const-string v2, ", songName=\'"

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v2, p0, Lcom/tw/music/b/a;->Bm:Ljava/lang/String;

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    const-string v2, ", albumName=\'"

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v2, p0, Lcom/tw/music/b/a;->Cm:Ljava/lang/String;

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    const-string v1, ", albumBitmap="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/tw/music/b/a;->Dm:Landroid/graphics/Bitmap;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    const-string v1, ", duration="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v1, p0, Lcom/tw/music/b/a;->duration:I

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v1, ", isPlaying="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-boolean v1, p0, Lcom/tw/music/b/a;->Em:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v1, ", isFavorite="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-boolean v1, p0, Lcom/tw/music/b/a;->oj:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v1, ", getShuffleRepeat="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v1, p0, Lcom/tw/music/b/a;->Fm:I

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v1, ", path="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p0, p0, Lcom/tw/music/b/a;->Vh:Ljava/lang/String;

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/16 p0, 0x7d

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public ua(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/b/a;->mCurrentPosition:I

    return-void
.end method

.method public va(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/b/a;->Fm:I

    return-void
.end method
