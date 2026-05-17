.class Lcom/eckom/xtlibrary/twproject/bt/bean/a;
.super Ljava/lang/Object;
.source "TWContact.java"

# interfaces
.implements Landroid/os/Parcelable$Creator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Landroid/os/Parcelable$Creator<",
        "Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;",
        ">;"
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public createFromParcel(Landroid/os/Parcel;)Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;
    .locals 0

    .line 2
    new-instance p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;-><init>(Landroid/os/Parcel;)V

    return-object p0
.end method

.method public bridge synthetic createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;
    .locals 0

    .line 1
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/bt/bean/a;->createFromParcel(Landroid/os/Parcel;)Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    move-result-object p0

    return-object p0
.end method

.method public newArray(I)[Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;
    .locals 0

    .line 2
    new-array p0, p1, [Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    return-object p0
.end method

.method public bridge synthetic newArray(I)[Ljava/lang/Object;
    .locals 0

    .line 1
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/bt/bean/a;->newArray(I)[Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    move-result-object p0

    return-object p0
.end method
