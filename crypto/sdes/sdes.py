# Simplified DES (S-DES) Implementation in Python using only built-in utilities

# Permutation Tables & S-Boxes
P10 = [3, 5, 2, 7, 4, 10, 1, 9, 8, 6]
P8 = [6, 3, 7, 4, 8, 5, 10, 9]
IP = [2, 6, 3, 1, 4, 8, 5, 7]
IP_INV = [4, 1, 3, 5, 7, 2, 8, 6]
EP = [4, 1, 2, 3, 2, 3, 4, 1]
P4 = [2, 4, 3, 1]

S0 = [
    [1, 0, 3, 2],
    [3, 2, 1, 0],
    [0, 2, 1, 3],
    [3, 1, 3, 2]
]

S1 = [
    [0, 1, 2, 3],
    [2, 0, 1, 3],
    [3, 0, 1, 0],
    [2, 1, 0, 3]
]


def permute(bits, table):
    """Applies a permutation table to a binary string."""
    return "".join(bits[i - 1] for i in table)


def left_shift(bits, count):
    """Performs a circular left shift on a binary string."""
    return bits[count:] + bits[:count]


def xor(bits1, bits2):
    """Performs bitwise XOR operation between two binary strings."""
    return "".join("1" if b1 != b2 else "0" for b1, b2 in zip(bits1, bits2))


def sbox_lookup(bits, sbox):
    """Finds 2-bit value from an S-Box matrix using outer bits as row and inner as col."""
    row = int(bits[0] + bits[3], 2)
    col = int(bits[1] + bits[2], 2)
    val = sbox[row][col]
    return f"{val:02b}"


def generate_keys(key_10bit):
    """Generates subkeys K1 and K2 from a 10-bit master key."""
    # Permutation P10
    p10_key = permute(key_10bit, P10)
    left, right = p10_key[:5], p10_key[5:]

    # LS-1 (Left Shift 1)
    left_ls1 = left_shift(left, 1)
    right_ls1 = left_shift(right, 1)
    k1 = permute(left_ls1 + right_ls1, P8)

    # LS-2 (Left Shift 2)
    left_ls2 = left_shift(left_ls1, 2)
    right_ls2 = left_shift(right_ls1, 2)
    k2 = permute(left_ls2 + right_ls2, P8)

    return k1, k2


def fk(bits_8bit, key_8bit):
    """S-DES Round function fK."""
    left, right = bits_8bit[:4], bits_8bit[4:]

    # Expand and Permute (E/P) right half
    ep = permute(right, EP)

    # XOR with subkey
    xored = xor(ep, key_8bit)

    # S-Box substitutions
    s0_out = sbox_lookup(xored[:4], S0)
    s1_out = sbox_lookup(xored[4:], S1)

    # Permutation P4
    p4_out = permute(s0_out + s1_out, P4)

    # XOR with left half
    new_left = xor(left, p4_out)

    return new_left + right


def switch(bits_8bit):
    """Swaps the 4-bit left and right halves."""
    return bits_8bit[4:] + bits_8bit[:4]


def encrypt(plaintext_8bit, k1, k2):
    """Encrypts an 8-bit block using S-DES."""
    ip = permute(plaintext_8bit, IP)
    fk1 = fk(ip, k1)
    sw = switch(fk1)
    fk2 = fk(sw, k2)
    ciphertext = permute(fk2, IP_INV)
    return ciphertext


def decrypt(ciphertext_8bit, k1, k2):
    """Decrypts an 8-bit block using S-DES."""
    ip = permute(ciphertext_8bit, IP)
    fk1 = fk(ip, k2)  # Use K2 first during decryption
    sw = switch(fk1)
    fk2 = fk(sw, k1)  # Use K1 second during decryption
    plaintext = permute(fk2, IP_INV)
    return plaintext


if __name__ == "__main__":
    # Input 10-bit Key and 8-bit Plaintext
    master_key = "1010000010"
    plaintext = "10010111"

    print("================ S-DES ALGORITHM ================")
    print(f"Master Key (10-bit) : {master_key}")
    print(f"Plaintext  (8-bit)  : {plaintext}\n")

    # Key Generation
    k1, k2 = generate_keys(master_key)
    print(f"Subkey K1           : {k1}")
    print(f"Subkey K2           : {k2}\n")

    # Encryption
    ciphertext = encrypt(plaintext, k1, k2)
    print(f"Encrypted Ciphertext: {ciphertext}")

    # Decryption
    decrypted_text = decrypt(ciphertext, k1, k2)
    print(f"Decrypted Plaintext : {decrypted_text}")
    print("=================================================")