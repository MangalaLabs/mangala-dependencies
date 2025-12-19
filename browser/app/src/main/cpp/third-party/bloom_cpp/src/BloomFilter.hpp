
#include <iostream>
#include <string>
#include <vector>
#include <iterator>

using namespace std;

typedef char BlockType;
typedef basic_istream<BlockType> BinaryInputStream;
typedef basic_ostream<BlockType> BinaryOutputStream;

/*
 Bloom filter with djb2 and sdbm hashing. It is a loose C++ port of
 the js library at https://github.com/cry/jsbloom
 */
class BloomFilter {

public:
    BloomFilter(size_t maxItems, double targetProbability);

    BloomFilter(const string &importFilePath, size_t bitCount, size_t maxItems);

    BloomFilter(BinaryInputStream &in, size_t bitCount, size_t maxItems);

    void add(const string &element);

    bool contains(const string &element);

    void writeToFile(const string &exportFilePath);

    void writeToStream(BinaryOutputStream &out);

    size_t getBitCount() const;

private:
    size_t bitCount;
    vector<BlockType> bloomVector;
    size_t hashRounds;
};
