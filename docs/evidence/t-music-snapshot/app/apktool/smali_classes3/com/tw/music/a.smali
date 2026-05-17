.class Lcom/tw/music/a;
.super Landroid/content/AsyncQueryHandler;
.source "AudioPreview.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/AudioPreview;->onCreate(Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/AudioPreview;


# direct methods
.method constructor <init>(Lcom/tw/music/AudioPreview;Landroid/content/ContentResolver;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/a;->this$0:Lcom/tw/music/AudioPreview;

    invoke-direct {p0, p2}, Landroid/content/AsyncQueryHandler;-><init>(Landroid/content/ContentResolver;)V

    return-void
.end method


# virtual methods
.method protected onQueryComplete(ILjava/lang/Object;Landroid/database/Cursor;)V
    .locals 6

    const-string p1, "AudioPreview"

    if-eqz p3, :cond_3

    .line 1
    invoke-interface {p3}, Landroid/database/Cursor;->moveToFirst()Z

    move-result p2

    if-eqz p2, :cond_3

    const-string p2, "title"

    .line 2
    invoke-interface {p3, p2}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    move-result p2

    const-string v0, "artist"

    .line 3
    invoke-interface {p3, v0}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    move-result v0

    const-string v1, "_id"

    .line 4
    invoke-interface {p3, v1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    move-result v1

    const-string v2, "_display_name"

    .line 5
    invoke-interface {p3, v2}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    move-result v2

    if-ltz v1, :cond_0

    .line 6
    iget-object v3, p0, Lcom/tw/music/a;->this$0:Lcom/tw/music/AudioPreview;

    invoke-interface {p3, v1}, Landroid/database/Cursor;->getLong(I)J

    move-result-wide v4

    invoke-static {v3, v4, v5}, Lcom/tw/music/AudioPreview;->a(Lcom/tw/music/AudioPreview;J)J

    :cond_0
    if-ltz p2, :cond_1

    .line 7
    invoke-interface {p3, p2}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;

    move-result-object p1

    .line 8
    iget-object p2, p0, Lcom/tw/music/a;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p2}, Lcom/tw/music/AudioPreview;->d(Lcom/tw/music/AudioPreview;)Landroid/widget/TextView;

    move-result-object p2

    invoke-virtual {p2, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    if-ltz v0, :cond_4

    .line 9
    invoke-interface {p3, v0}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;

    move-result-object p1

    .line 10
    iget-object p2, p0, Lcom/tw/music/a;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p2}, Lcom/tw/music/AudioPreview;->e(Lcom/tw/music/AudioPreview;)Landroid/widget/TextView;

    move-result-object p2

    invoke-virtual {p2, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_0

    :cond_1
    if-ltz v2, :cond_2

    .line 11
    invoke-interface {p3, v2}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;

    move-result-object p1

    .line 12
    iget-object p2, p0, Lcom/tw/music/a;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p2}, Lcom/tw/music/AudioPreview;->d(Lcom/tw/music/AudioPreview;)Landroid/widget/TextView;

    move-result-object p2

    invoke-virtual {p2, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_0

    :cond_2
    const-string p2, "Cursor had no names for us"

    .line 13
    invoke-static {p1, p2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_0

    :cond_3
    const-string p2, "empty cursor"

    .line 14
    invoke-static {p1, p2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    :cond_4
    :goto_0
    if-eqz p3, :cond_5

    .line 15
    invoke-interface {p3}, Landroid/database/Cursor;->close()V

    .line 16
    :cond_5
    iget-object p0, p0, Lcom/tw/music/a;->this$0:Lcom/tw/music/AudioPreview;

    invoke-virtual {p0}, Lcom/tw/music/AudioPreview;->Oa()V

    return-void
.end method
