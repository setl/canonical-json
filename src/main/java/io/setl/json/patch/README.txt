As soon as arrays are in the mix, we need to solve the Longest Common Sequence (LCS) problem. Furthermore, we have to expand all sub-structures. This means the
number of elements we need to process can be quite large. With array lengths of n and m, the algorithm is rated as O(nm).

The basic solution for LCS is quadratic in time and space. This means that for larger number of elements, performance is a problem both CPU and memory wise. As
an example, comparing two arrays of approximately 10,000 items each takes 1.32 seconds on my iMac.

Daniel Hirschberg in his 1975 paper (A linear space algorithm for computing maximal common subsequences) showed how the problem could be solved using a
divide-and-conquer strategy in linear space, but the algorithm was slightly slower. The best performance was found from using this algorithm to partition the
problem into "reasonable" sized pieces which were then solved using the original algorith. This improved the performance on the 10,000 item array to 0.62
seconds on my iMac.

In 1977 Hirschberg introduced a method using "contours" to reduce the search space. Various authors used the method of contours to produce progressively better
algorithms. In 2000, Claus Rick published "Simple and fast computation of longest common subsequences" which describe an algorithm rated as O(min{pm,p(n-p)}
which uses the contour paradigm.


