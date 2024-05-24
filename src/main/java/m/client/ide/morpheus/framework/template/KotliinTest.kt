package m.client.ide.morpheus.framework.template

fun main(args: Array<String>) {
    val sum = { x: Int, y: Int -> println("Computing the sum of $x and $y...")
        x + y
    }

    val isUnit = println(sum(1, 2))
    println(isUnit);
}
