import JSEncrypt from 'jsencrypt/bin/jsencrypt'
const publicKey = `MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvrECm3a3O3FM83STPxTD
zWPx3E++Jls3h0BM314s1SZvEx1E90wqerBzN320UCljafd7rOOsaX6DsbjdOPpT
pvhLAbjt2e7i56XXAlDgHd81hbyd1apX1I23tyPPImtaAiCzQLsLf8qp0d4MYbG0
IBuJHCafCAtKiZdUqBrYAKiT/s/VFP0AwUmUJr5+ZrqdNIgUsdrnh6glAyjMhjzz
ehcBYDdl3XBH1uUH7mB7Iyjgk9JDlEBZA1Hx1UpRPCYYcSWTweJ5S3/mPdfdunjU
c9OWHY37rPoaWpCFdhfg8BU6VBu+gaseNgfI+z3hlDsjvEl5G0ywqjSpRAY+hz+j
YQIDAQAB`

export function encrypt(pass) {
    const encrypted = new JSEncrypt()
    encrypted.setPublicKey(publicKey)
    return encrypted.encrypt(pass)
}