.class public Lcom/eckom/xtlibrary/b/j/s;
.super Ljava/lang/Object;
.source "UtilsMedia.java"


# direct methods
.method public static a(Ljava/util/ArrayList;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/f/b/a;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/a;",
            ">;",
            "Ljava/lang/String;",
            ")",
            "Lcom/eckom/xtlibrary/b/f/b/a;"
        }
    .end annotation

    .line 1
    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/a;

    .line 2
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/a;->getName()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-object v0

    :cond_1
    const/4 p0, 0x0

    return-object p0
.end method

.method public static a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/f/b/d;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;",
            "Ljava/lang/String;",
            ")",
            "Lcom/eckom/xtlibrary/b/f/b/d;"
        }
    .end annotation

    .line 3
    invoke-virtual {p0}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/d;

    .line 4
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/d;->getName()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-object v0

    :cond_1
    const/4 p0, 0x0

    return-object p0
.end method

.method public static a(Ljava/util/ArrayList;Ljava/lang/String;III)Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/g;",
            ">;",
            "Ljava/lang/String;",
            "III)",
            "Lcom/eckom/xtlibrary/b/f/b/g;"
        }
    .end annotation

    if-nez p0, :cond_0

    const/4 p0, 0x0

    return-object p0

    .line 30
    :cond_0
    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-eqz v0, :cond_2

    .line 31
    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_2

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 32
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-static {v2, p1}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v2

    if-eqz v2, :cond_1

    return-object v1

    .line 33
    :cond_2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {v0, p1, p2, p3, p4}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 34
    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 35
    invoke-virtual {p0, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    return-object v0
.end method

.method public static a(Ljava/util/LinkedHashMap;Ljava/lang/String;II)Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/LinkedHashMap<",
            "Ljava/lang/String;",
            "Lcom/eckom/xtlibrary/b/f/b/g;",
            ">;",
            "Ljava/lang/String;",
            "II)",
            "Lcom/eckom/xtlibrary/b/f/b/g;"
        }
    .end annotation

    if-nez p0, :cond_0

    const/4 p0, 0x0

    return-object p0

    .line 26
    :cond_0
    invoke-virtual {p0, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    if-nez v0, :cond_1

    .line 27
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v1, 0x0

    invoke-direct {v0, p1, p2, p3, v1}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 28
    invoke-virtual {p0, p1, v0}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 29
    invoke-virtual {p0, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p0

    move-object v0, p0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_1
    return-object v0
.end method

.method public static a(Ljava/util/ArrayList;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/concurrent/CopyOnWriteArrayList;)V
    .locals 7
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/g;",
            ">;",
            "Lcom/eckom/xtlibrary/b/f/b/g;",
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;)V"
        }
    .end annotation

    .line 14
    invoke-virtual {p0}, Ljava/util/ArrayList;->clear()V

    .line 15
    invoke-virtual {p2}, Ljava/util/concurrent/CopyOnWriteArrayList;->size()I

    move-result v0

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 16
    invoke-virtual {p2}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p2

    :goto_0
    invoke-interface {p2}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/d;

    .line 17
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/d;->tc()Ljava/util/ArrayList;

    move-result-object v1

    .line 18
    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/d;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v4

    invoke-direct {v2, v3, v4}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;I)V

    invoke-virtual {p1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    .line 19
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/d;->getKey()Ljava/lang/String;

    move-result-object v2

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 20
    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/d;->getName()Ljava/lang/String;

    move-result-object v3

    iget v4, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    iget v5, p1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    const/4 v6, 0x1

    invoke-direct {v2, v3, v4, v5, v6}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 21
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v3

    invoke-virtual {v2, v3}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 22
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/d;->getKey()Ljava/lang/String;

    move-result-object v0

    iput-object v0, v2, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 23
    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_0

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 24
    invoke-virtual {v2, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    goto :goto_1

    .line 25
    :cond_0
    invoke-virtual {p0, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_0

    :cond_1
    return-void
.end method

.method public static a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V
    .locals 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;",
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;)V"
        }
    .end annotation

    .line 5
    invoke-virtual {p0}, Ljava/util/concurrent/CopyOnWriteArrayList;->size()I

    move-result v0

    if-nez v0, :cond_0

    .line 6
    invoke-virtual {p0, p1}, Ljava/util/concurrent/CopyOnWriteArrayList;->addAll(Ljava/util/Collection;)Z

    goto :goto_2

    .line 7
    :cond_0
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    const/4 v1, 0x0

    .line 8
    :goto_0
    invoke-virtual {p1}, Ljava/util/concurrent/CopyOnWriteArrayList;->size()I

    move-result v2

    if-ge v1, v2, :cond_2

    .line 9
    invoke-virtual {p1, v1}, Ljava/util/concurrent/CopyOnWriteArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/d;

    .line 10
    invoke-virtual {v2}, Lcom/eckom/xtlibrary/b/f/b/d;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-static {p0, v3}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/f/b/d;

    move-result-object v3

    if-eqz v3, :cond_1

    .line 11
    invoke-virtual {v3}, Lcom/eckom/xtlibrary/b/f/b/d;->tc()Ljava/util/ArrayList;

    move-result-object v3

    invoke-virtual {v2}, Lcom/eckom/xtlibrary/b/f/b/d;->tc()Ljava/util/ArrayList;

    move-result-object v2

    invoke-virtual {v3, v2}, Ljava/util/ArrayList;->addAll(Ljava/util/Collection;)Z

    goto :goto_1

    .line 12
    :cond_1
    invoke-virtual {v0, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    :goto_1
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 13
    :cond_2
    invoke-virtual {p0, v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->addAll(Ljava/util/Collection;)Z

    :goto_2
    return-void
.end method

.method public static b(Ljava/util/ArrayList;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/f/b/b;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/b;",
            ">;",
            "Ljava/lang/String;",
            ")",
            "Lcom/eckom/xtlibrary/b/f/b/b;"
        }
    .end annotation

    .line 1
    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/b;

    .line 2
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/b;->getName()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-object v0

    :cond_1
    const/4 p0, 0x0

    return-object p0
.end method

.method public static c(Ljava/util/ArrayList;Ljava/lang/String;)Z
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/a;",
            ">;",
            "Ljava/lang/String;",
            ")Z"
        }
    .end annotation

    .line 1
    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/a;

    .line 2
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/a;->getName()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 p0, 0x1

    return p0

    :cond_1
    const/4 p0, 0x0

    return p0
.end method

.method public static d(Ljava/util/ArrayList;Ljava/lang/String;)Z
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/b;",
            ">;",
            "Ljava/lang/String;",
            ")Z"
        }
    .end annotation

    .line 1
    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/b;

    .line 2
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/b;->getName()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 p0, 0x1

    return p0

    :cond_1
    const/4 p0, 0x0

    return p0
.end method
