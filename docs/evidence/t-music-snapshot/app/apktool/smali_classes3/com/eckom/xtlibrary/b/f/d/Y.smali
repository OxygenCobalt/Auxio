.class Lcom/eckom/xtlibrary/b/f/d/Y;
.super Ljava/lang/Object;
.source "MusicModel.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/ba;->Ea(Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic sk:Ljava/lang/String;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/ba;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/ba;Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/Y;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/Y;->sk:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/Y;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/Y;->sk:Ljava/lang/String;

    invoke-static {v0, p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(Lcom/eckom/xtlibrary/b/f/d/ba;Ljava/lang/String;)V

    return-void
.end method
