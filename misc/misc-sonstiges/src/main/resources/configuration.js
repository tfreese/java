if (new Date().getDay() == 0)
{
	conf.threads = 100;
}
else
{
	conf.threads = 5;
}

conf.blockSize = 4096;
