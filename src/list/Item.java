package list;

public class Item
{
    public int Value;
    public Item Next;

    public Item(int value, Item next) {
        Value = value;
        Next = next;
    }

    public Item(Item item)
    {
        Value = item.Value;
    }
}
