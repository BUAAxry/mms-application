@Test
public void outputHasLineNumbers() {
   String content = "1st match on #1\nand\n2nd match on #3";
   String out = grep.grep("match", "test.txt", content);
   assertThat(out.indexOf("test.txt:1 1st match"), is(not(-1)));
   assertThat(out.indexOf("test.txt:3 2nd match"), is(not(-1)));
}