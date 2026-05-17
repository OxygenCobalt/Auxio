.class public Lcom/eckom/xtlibrary/b/h/a;
.super Ljava/lang/Object;
.source "RadioDataHolder.java"


# static fields
.field private static instance:Lcom/eckom/xtlibrary/b/h/a;


# instance fields
.field public Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

.field public Jk:I

.field public Kk:I

.field public Lk:I

.field public Mk:I

.field public Nk:I

.field public Ok:I

.field public Pk:I

.field public Qk:I

.field public Rk:I

.field public Sk:I

.field public Tk:I

.field public Uk:I

.field public Vk:I

.field public Wk:I

.field public Xk:I

.field public Yk:Z

.field public Zk:Z

.field public _k:Z

.field public cl:Z

.field public dl:Z

.field public el:Z

.field public fl:Z

.field public gl:Ljava/lang/String;

.field public hl:Ljava/lang/String;

.field public il:Z

.field public jl:I

.field public kl:I

.field public ll:I

.field public mRegion:I

.field public mSource:I

.field public ml:Landroid/graphics/drawable/Drawable;

.field public nl:I

.field public ol:Z

.field public pl:Ljava/lang/String;

.field public ql:I

.field public rl:[Ljava/lang/String;


# direct methods
.method private constructor <init>()V
    .locals 35

    move-object/from16 v0, p0

    .line 1
    invoke-direct/range {p0 .. p0}, Ljava/lang/Object;-><init>()V

    const/16 v1, 0x2a30

    .line 2
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Jk:I

    const/16 v2, 0x222e

    .line 3
    iput v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Kk:I

    .line 4
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    .line 5
    iput v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    const/16 v1, 0x65d

    .line 6
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Nk:I

    const/16 v1, 0x20a

    .line 7
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Ok:I

    const/4 v1, 0x5

    .line 8
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Pk:I

    .line 9
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Qk:I

    const/16 v1, 0x9

    .line 10
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Rk:I

    .line 11
    iput v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Wk:I

    const-string v3, "  None  "

    const-string v4, "  News  "

    const-string v5, "Affairs "

    const-string v6, "  Info  "

    const-string v7, " Sport  "

    const-string v8, "Educate "

    const-string v9, " Drama  "

    const-string v10, "Culture "

    const-string v11, "Science "

    const-string v12, " Varied "

    const-string v13, " Pop M  "

    const-string v14, " Rock M "

    const-string v15, " Easy M "

    const-string v16, "Light M "

    const-string v17, "Classics"

    const-string v18, "Other M "

    const-string v19, "Weather "

    const-string v20, "Finance "

    const-string v21, "Children"

    const-string v22, " Social "

    const-string v23, "Religion"

    const-string v24, "Phone In"

    const-string v25, " Travel "

    const-string v26, "Leisure "

    const-string v27, "  Jazz  "

    const-string v28, "Country "

    const-string v29, "Nation M"

    const-string v30, " Oldies "

    const-string v31, " Folk M "

    const-string v32, "Document"

    const-string v33, "  Test  "

    const-string v34, " Alarm  "

    .line 12
    filled-new-array/range {v3 .. v34}, [Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/h/a;->rl:[Ljava/lang/String;

    .line 13
    invoke-direct/range {p0 .. p0}, Lcom/eckom/xtlibrary/b/h/a;->_e()V

    return-void
.end method

.method private _e()V
    .locals 4

    const/16 v0, 0x12

    new-array v1, v0, [Lcom/eckom/xtlibrary/b/h/a/a;

    .line 1
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    const/4 v1, 0x0

    :goto_0
    if-ge v1, v0, :cond_0

    .line 2
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    new-instance v3, Lcom/eckom/xtlibrary/b/h/a/a;

    invoke-direct {v3}, Lcom/eckom/xtlibrary/b/h/a/a;-><init>()V

    aput-object v3, v2, v1

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    :cond_0
    return-void
.end method

.method public static getInstance()Lcom/eckom/xtlibrary/b/h/a;
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/h/a;->instance:Lcom/eckom/xtlibrary/b/h/a;

    if-nez v0, :cond_1

    .line 2
    const-class v0, Lcom/eckom/xtlibrary/b/h/a;

    monitor-enter v0

    .line 3
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/h/a;->instance:Lcom/eckom/xtlibrary/b/h/a;

    if-nez v1, :cond_0

    .line 4
    new-instance v1, Lcom/eckom/xtlibrary/b/h/a;

    invoke-direct {v1}, Lcom/eckom/xtlibrary/b/h/a;-><init>()V

    sput-object v1, Lcom/eckom/xtlibrary/b/h/a;->instance:Lcom/eckom/xtlibrary/b/h/a;

    .line 5
    :cond_0
    monitor-exit v0

    goto :goto_0

    :catchall_0
    move-exception v1

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1

    .line 6
    :cond_1
    :goto_0
    sget-object v0, Lcom/eckom/xtlibrary/b/h/a;->instance:Lcom/eckom/xtlibrary/b/h/a;

    return-object v0
.end method
