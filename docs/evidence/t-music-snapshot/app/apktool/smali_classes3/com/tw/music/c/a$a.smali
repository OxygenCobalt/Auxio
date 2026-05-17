.class public Lcom/tw/music/c/a$a;
.super Ljava/lang/Object;
.source "ListPlugin.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/c/a;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "a"
.end annotation


# instance fields
.field private Im:Landroid/graphics/drawable/Drawable;

.field private Jm:Landroid/graphics/drawable/Drawable;

.field private Km:Ljava/lang/String;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public c(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a$a;->Jm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public d(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a$a;->Im:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public ld()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a$a;->Jm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public md()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a$a;->Im:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public pb(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a$a;->Km:Ljava/lang/String;

    return-void
.end method
