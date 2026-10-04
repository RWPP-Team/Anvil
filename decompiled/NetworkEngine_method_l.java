	public strictfp void l(String string) {
		boolean var2 = true;
		String var3 = null;
		if (string != null) {
			String var4 = string.trim();
			if ((var4.startsWith("-") || var4.startsWith(".") || var4.startsWith("_")) && var4.length() >= 2) {
				String var5 = var4.substring(1).trim();
				int var6 = var5.indexOf(" ");
				if (var6 == -1) {
					var6 = var5.length();
				}

				var3 = var5.substring(0, var6).toLowerCase(Locale.ENGLISH);
			}
		}

		if ("share".equals(var3)) {
			var2 = false;
		}

		if ("t".equals(var3)) {
			var2 = false;
		}

		if (var2) {
			string = "-t " + string;
		}

		this.sendChatMessage(string);
	}
