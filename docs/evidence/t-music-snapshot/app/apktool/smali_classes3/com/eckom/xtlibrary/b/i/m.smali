.class public Lcom/eckom/xtlibrary/b/i/m;
.super Ljava/lang/Object;
.source "ThemeSwitchInfo.java"


# annotations
.annotation build Landroid/annotation/TargetApi;
    value = 0x13
.end annotation


# instance fields
.field private Mc:Lcom/eckom/xtlibrary/b/i/l;

.field private dm:Ljava/lang/String;

.field public em:I


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x2

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/i/m;->em:I

    return-void
.end method


# virtual methods
.method public Nc()Lcom/eckom/xtlibrary/b/i/l;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/m;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    return-object p0
.end method

.method public Oc()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/m;->dm:Ljava/lang/String;

    return-object p0
.end method

.method public Pc()V
    .locals 0

    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/i/l;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/m;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    return-void
.end method
