.class public final enum Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;
.super Ljava/lang/Enum;
.source "BaseVideoMode.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x4019
    name = "VIDEO_MODEL_STATE"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Enum<",
        "Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;",
        ">;"
    }
.end annotation


# static fields
.field private static final synthetic $VALUES:[Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

.field public static final enum NULL:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

.field public static final enum VIDEO_MODEL_CREATE:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

.field public static final enum VIDEO_MODEL_DESTROY:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

.field public static final enum VIDEO_MODEL_PAUSE:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

.field public static final enum VIDEO_MODEL_RESUME:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;


# direct methods
.method static constructor <clinit>()V
    .locals 7

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    const/4 v1, 0x0

    const-string v2, "NULL"

    invoke-direct {v0, v2, v1}, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->NULL:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    const/4 v2, 0x1

    const-string v3, "VIDEO_MODEL_CREATE"

    invoke-direct {v0, v3, v2}, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_CREATE:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    .line 3
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    const/4 v3, 0x2

    const-string v4, "VIDEO_MODEL_RESUME"

    invoke-direct {v0, v4, v3}, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_RESUME:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    .line 4
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    const/4 v4, 0x3

    const-string v5, "VIDEO_MODEL_PAUSE"

    invoke-direct {v0, v5, v4}, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_PAUSE:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    .line 5
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    const/4 v5, 0x4

    const-string v6, "VIDEO_MODEL_DESTROY"

    invoke-direct {v0, v6, v5}, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_DESTROY:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    const/4 v0, 0x5

    new-array v0, v0, [Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    .line 6
    sget-object v6, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->NULL:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    aput-object v6, v0, v1

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_CREATE:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    aput-object v1, v0, v2

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_RESUME:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    aput-object v1, v0, v3

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_PAUSE:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    aput-object v1, v0, v4

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_DESTROY:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    aput-object v1, v0, v5

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->$VALUES:[Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()V"
        }
    .end annotation

    .line 1
    invoke-direct {p0, p1, p2}, Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V

    return-void
.end method

.method public static valueOf(Ljava/lang/String;)Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;
    .locals 1

    .line 1
    const-class v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    invoke-static {v0, p0}, Ljava/lang/Enum;->valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    return-object p0
.end method

.method public static values()[Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->$VALUES:[Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    invoke-virtual {v0}, [Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->clone()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    return-object v0
.end method
