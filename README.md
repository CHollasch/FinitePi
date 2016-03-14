# FinitePi

Uses the popular Machin-Like algorithm to great amounts of precision in calculating &pi;.
The Machin-Like formula proposed by Hwang Chien-Lih from 1997 is currently being used.

![Machin-Like formula](https://upload.wikimedia.org/math/0/6/2/062523cddd6778ddcb608e669abc210f.png)

By implementing a GUI, calculations can be made to relatively large decimal precision values and progress can be monitored during these calculations. 

Inverse tangent is calculated using the following Taylor series. Javas BigDecimal class is used to get arbitrarily large decimal precision when calculating inverse tangent (along with every other equation used to find pi).

![Arctan](https://upload.wikimedia.org/math/a/c/8/ac88e3ffdfbbac530b136f83211a87f7.png)
