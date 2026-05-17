.class public Lcom/tw/music/MusicService$a;
.super Landroid/os/Binder;
.source "MusicService.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/MusicService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = "a"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/MusicService;


# direct methods
.method public constructor <init>(Lcom/tw/music/MusicService;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/MusicService$a;->this$0:Lcom/tw/music/MusicService;

    invoke-direct {p0}, Landroid/os/Binder;-><init>()V

    return-void
.end method


# virtual methods
.method public getService()Lcom/tw/music/MusicService;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/MusicService$a;->this$0:Lcom/tw/music/MusicService;

    return-object p0
.end method
