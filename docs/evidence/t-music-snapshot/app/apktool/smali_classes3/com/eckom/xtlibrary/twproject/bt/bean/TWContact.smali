.class public Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;
.super Ljava/lang/Object;
.source "TWContact.java"

# interfaces
.implements Landroid/os/Parcelable;


# static fields
.field public static final CREATOR:Landroid/os/Parcelable$Creator;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/os/Parcelable$Creator<",
            "Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;",
            ">;"
        }
    .end annotation
.end field


# instance fields
.field protected Yf:Ljava/lang/String;

.field protected Zf:Ljava/lang/String;

.field protected _f:Ljava/lang/String;

.field protected cg:Z

.field protected id:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/twproject/bt/bean/a;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/twproject/bt/bean/a;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->CREATOR:Landroid/os/Parcelable$Creator;

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method protected constructor <init>(Landroid/os/Parcel;)V
    .locals 1

    .line 6
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 7
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    move-result v0

    iput v0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->id:I

    .line 8
    invoke-virtual {p1}, Landroid/os/Parcel;->readString()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Yf:Ljava/lang/String;

    .line 9
    invoke-virtual {p1}, Landroid/os/Parcel;->readString()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    .line 10
    invoke-virtual {p1}, Landroid/os/Parcel;->readString()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->_f:Ljava/lang/String;

    .line 11
    invoke-virtual {p1}, Landroid/os/Parcel;->readByte()B

    move-result p1

    if-eqz p1, :cond_0

    const/4 p1, 0x1

    goto :goto_0

    :cond_0
    const/4 p1, 0x0

    :goto_0
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->cg:Z

    return-void
.end method

.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    .line 2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 3
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Yf:Ljava/lang/String;

    .line 4
    iput-object p2, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    .line 5
    iput-object p3, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->_f:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method public A(Z)V
    .locals 0

    .line 1
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->cg:Z

    return-void
.end method

.method public describeContents()I
    .locals 0

    const/4 p0, 0x0

    return p0
.end method

.method public eb()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    return-object p0
.end method

.method public equals(Ljava/lang/Object;)Z
    .locals 1

    .line 1
    :try_start_0
    instance-of v0, p1, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    if-eqz v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    check-cast p1, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    invoke-virtual {p0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    return p0

    :catch_0
    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method public getContactName()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Yf:Ljava/lang/String;

    return-object p0
.end method

.method public setId(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->id:I

    return-void
.end method

.method public toString()Ljava/lang/String;
    .locals 3

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "TWContact{id="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->id:I

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v1, ", contactName=\'"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Yf:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/16 v1, 0x27

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    const-string v2, ", contactNumber=\'"

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    const-string v2, ", contactPin=\'"

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->_f:Ljava/lang/String;

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    const-string v1, ", favorite="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->cg:Z

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const/16 p0, 0x7d

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public wa(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Yf:Ljava/lang/String;

    return-void
.end method

.method public writeToParcel(Landroid/os/Parcel;I)V
    .locals 0

    .line 1
    iget p2, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->id:I

    invoke-virtual {p1, p2}, Landroid/os/Parcel;->writeInt(I)V

    .line 2
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Yf:Ljava/lang/String;

    invoke-virtual {p1, p2}, Landroid/os/Parcel;->writeString(Ljava/lang/String;)V

    .line 3
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    invoke-virtual {p1, p2}, Landroid/os/Parcel;->writeString(Ljava/lang/String;)V

    .line 4
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->_f:Ljava/lang/String;

    invoke-virtual {p1, p2}, Landroid/os/Parcel;->writeString(Ljava/lang/String;)V

    .line 5
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->cg:Z

    int-to-byte p0, p0

    invoke-virtual {p1, p0}, Landroid/os/Parcel;->writeByte(B)V

    return-void
.end method

.method public xa(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->Zf:Ljava/lang/String;

    return-void
.end method

.method public ya(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->_f:Ljava/lang/String;

    return-void
.end method
