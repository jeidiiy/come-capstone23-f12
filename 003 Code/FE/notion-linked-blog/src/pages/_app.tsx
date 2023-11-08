import wrapper from "@/redux/store";
import {createGlobalStyle} from "styled-components";
import {Provider} from "react-redux";
import {useState} from "react";
import {ConfigProvider, theme} from "antd";
import AppHeader from "@/components/common/AppHeader";

const {darkAlgorithm, defaultAlgorithm} = theme;

const GlobalStyles = createGlobalStyle`
	html,
	body {
		margin: 0;
		padding: 0;
	}
`;

function App({Component, ...rest}) {
	const {store, props} = wrapper.useWrappedStore(rest);
	const [isDark, setIsDark] = useState(false);

	return (
		<ConfigProvider
			theme={{algorithm: isDark ? darkAlgorithm : defaultAlgorithm}}>
			<Provider store={store}>
				<GlobalStyles />
				<AppHeader isDark={isDark} setIsDark={setIsDark} />
				<Component {...props.pageProps} isDark={isDark} />
			</Provider>
		</ConfigProvider>
	);
}

export default App;
