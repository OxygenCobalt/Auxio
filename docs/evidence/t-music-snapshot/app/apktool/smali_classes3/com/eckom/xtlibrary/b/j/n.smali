.class public Lcom/eckom/xtlibrary/b/j/n;
.super Ljava/lang/Object;
.source "RsaUtil.java"


# direct methods
.method public static a(ILjavax/crypto/Cipher;[BILjava/io/ByteArrayOutputStream;)V
    .locals 2

    const/4 v0, 0x0

    :goto_0
    sub-int v1, p3, v0

    if-lez v1, :cond_1

    if-lt v1, p0, :cond_0

    .line 1
    invoke-virtual {p1, p2, v0, p0}, Ljavax/crypto/Cipher;->doFinal([BII)[B

    move-result-object v1

    add-int/2addr v0, p0

    goto :goto_1

    .line 2
    :cond_0
    invoke-virtual {p1, p2, v0, v1}, Ljavax/crypto/Cipher;->doFinal([BII)[B

    move-result-object v1

    move v0, p3

    .line 3
    :goto_1
    invoke-virtual {p4, v1}, Ljava/io/ByteArrayOutputStream;->write([B)V

    goto :goto_0

    :cond_1
    return-void
.end method

.method public static decrypt(Ljava/lang/String;)Ljava/lang/String;
    .locals 4

    const-string v0, "RSA/ECB/PKCS1Padding"

    .line 1
    invoke-static {v0}, Ljavax/crypto/Cipher;->getInstance(Ljava/lang/String;)Ljavax/crypto/Cipher;

    move-result-object v0

    const-string v1, "RSA"

    const-string v2, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmyWXcwD/iJ4mJyWgKTEoIhDCPnFRV4wTNssNnd1RYbJbJ9264Jua9GgqWe3hC2qz3K3xk6CrqMBiwphIBkOLzVAmFGpskKQgndqZviXNJ2tNEYH0MoO9jcQo1DsIQHoYFyYoBdvh4WqECdghLTBBoXU6SOnSMIlVk+xIw1uKpXQIDAQAB"

    .line 2
    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/j/n;->z(Ljava/lang/String;Ljava/lang/String;)Ljava/security/PublicKey;

    move-result-object v1

    const/4 v2, 0x2

    invoke-virtual {v0, v2, v1}, Ljavax/crypto/Cipher;->init(ILjava/security/Key;)V

    .line 3
    invoke-static {p0, v2}, Landroid/util/Base64;->decode(Ljava/lang/String;I)[B

    move-result-object p0

    .line 4
    array-length v1, p0

    .line 5
    new-instance v2, Ljava/io/ByteArrayOutputStream;

    invoke-direct {v2}, Ljava/io/ByteArrayOutputStream;-><init>()V

    const/16 v3, 0x400

    .line 6
    invoke-static {v3, v0, p0, v1, v2}, Lcom/eckom/xtlibrary/b/j/n;->a(ILjavax/crypto/Cipher;[BILjava/io/ByteArrayOutputStream;)V

    .line 7
    invoke-virtual {v2}, Ljava/io/ByteArrayOutputStream;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static h(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z
    .locals 2

    .line 1
    new-instance v0, Lorg/json/JSONObject;

    invoke-direct {v0, p0}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string p0, "softwareType"

    .line 2
    invoke-virtual {v0, p0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p0

    const-string v1, "cid"

    .line 3
    invoke-virtual {v0, v1}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 4
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v1

    if-nez v1, :cond_0

    invoke-virtual {p1, p0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p0

    if-eqz p0, :cond_0

    invoke-static {v0, p2}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result p0

    if-eqz p0, :cond_0

    const/4 p0, 0x1

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method public static z(Ljava/lang/String;Ljava/lang/String;)Ljava/security/PublicKey;
    .locals 1

    const/4 v0, 0x2

    .line 1
    invoke-static {p1, v0}, Landroid/util/Base64;->decode(Ljava/lang/String;I)[B

    move-result-object p1

    .line 2
    invoke-static {p0}, Ljava/security/KeyFactory;->getInstance(Ljava/lang/String;)Ljava/security/KeyFactory;

    move-result-object p0

    .line 3
    new-instance v0, Ljava/security/spec/X509EncodedKeySpec;

    invoke-direct {v0, p1}, Ljava/security/spec/X509EncodedKeySpec;-><init>([B)V

    .line 4
    invoke-virtual {p0, v0}, Ljava/security/KeyFactory;->generatePublic(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;

    move-result-object p0

    return-object p0
.end method
