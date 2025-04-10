from setuptools import find_packages, setup

package_name = 'test_pack'

setup(
    name=package_name,
    version='0.0.0',
    packages=find_packages(exclude=['test']),
    data_files=[
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
        ('share/test_pack/launch', ['launch/turtlesim_launch_gen.py']),
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='rvv',
    maintainer_email='rvv@todo.todo',
    description='TODO: Package description',
    license='TODO: License declaration',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
        	'lab2 = test_pack.lab2:main',
        ],
    },
)
